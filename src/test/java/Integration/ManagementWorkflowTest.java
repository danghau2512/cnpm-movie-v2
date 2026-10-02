package Integration;

import Controller.*;
import Dao.*;
import Filter.ManagementFilter;
import Model.Movie;
import Model.Showtime;
import Service.*;
import Util.JdbiConnector;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

/** Chỉ chạy khi yêu cầu rõ ràng; mọi ghi dữ liệu đều nằm trong database tạm riêng. */
@EnabledIfSystemProperty(named = "cinebook.integration", matches = "true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagementWorkflowTest {
    private final String database = "cinebook_test_" + UUID.randomUUID().toString().replace("-", "");
    private Jdbi source, db;
    private Tomcat tomcat;
    private String base;
    private AdminService admin;
    private BookingManagementDAO bookings;
    private PaymentDAO payments;

    @BeforeAll void setup() throws Exception {
        source = JdbiConnector.getJdbi();
        source.useHandle(h -> h.execute("CREATE DATABASE `" + database + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"));
        Properties props = new Properties();
        try (var in = Files.newInputStream(Path.of("src/main/resources/db.properties"))) { props.load(in); }
        URI original = URI.create(props.getProperty("db.url").substring("jdbc:".length()));
        String url = "jdbc:mysql://" + original.getRawAuthority() + "/" + database
                + (original.getRawQuery() == null ? "" : "?" + original.getRawQuery());
        db = Jdbi.create(url, props.getProperty("db.user"), props.getProperty("db.password"));
        for (String table : List.of("users", "movies", "genres", "movie_genres", "rooms", "showtimes", "seats", "bookings", "booking_seats", "payments")) {
            String ddl = source.withHandle(h -> h.createQuery("SHOW CREATE TABLE " + table).map((rs, ctx) -> rs.getString(2)).one());
            db.useHandle(h -> h.execute(ddl));
        }
        db.useHandle(h -> {
            h.execute("INSERT INTO users(id,full_name,email,password_hash,role,status) VALUES (1,'Admin','admin@test.local','admin123','ADMIN','ACTIVE'),(2,'Customer','customer@test.local','customer123','CUSTOMER','ACTIVE')");
            h.execute("INSERT INTO rooms(id,name,total_seats,status) VALUES (1,'Phòng test',2,'ACTIVE')");
            h.execute("INSERT INTO genres(id,name) VALUES (1,'Hành động')");
            h.execute("INSERT INTO seats(id,room_id,seat_row,seat_number,seat_code) VALUES (1,1,'A',1,'A1'),(2,1,'A',2,'A2')");
        });
        admin = new AdminService(new AdminDAO(db)); bookings = new BookingManagementDAO(db); payments = new PaymentDAO(db);
        tomcat = new Tomcat(); tomcat.setBaseDir(Files.createTempDirectory("cinebook-tomcat-").toString()); tomcat.setPort(0);
        tomcat.getConnector();
        Context context = tomcat.addWebapp("/cinebook", Path.of("src/main/webapp").toAbsolutePath().toString());
        context.setParentClassLoader(getClass().getClassLoader()); context.setIgnoreAnnotations(true);
        Tomcat.addServlet(context, "auth", new AuthController(new AuthService(new UserDAO(db))));
        for (String path : List.of("/login", "/register", "/logout")) context.addServletMappingDecoded(path, "auth");
        Tomcat.addServlet(context, "admin", new AdminController(admin, new BookingManagementService(bookings)));
        for (String path : List.of("/admin/dashboard", "/admin/movies", "/admin/showtimes", "/admin/bookings")) context.addServletMappingDecoded(path, "admin");
        Tomcat.addServlet(context, "history", new BookingHistoryController(new BookingManagementService(bookings)));
        context.addServletMappingDecoded("/booking-history", "history");
        Tomcat.addServlet(context, "payment", new PaymentController(new PaymentService(payments)));
        for (String path : List.of("/payment", "/payment-result")) context.addServletMappingDecoded(path, "payment");
        Tomcat.addServlet(context, "jspSmoke", new jakarta.servlet.http.HttpServlet() {
            @Override protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
                    throws jakarta.servlet.ServletException, java.io.IOException {
                String page = req.getParameter("page");
                if (!List.of("movies", "movie-detail", "showtimes", "booking").contains(page)) { res.sendError(404); return; }
                Movie m = movie("<script>alert('test')</script>"); m.setId(99);
                m.setTrailerUrl("https://example.com/trailer?a=1&b=2");
                req.setAttribute("movie", m); req.setAttribute("movies", List.of(m));
                req.setAttribute("genres", List.of("Hành động")); req.setAttribute("keyword", "");
                Showtime s = showtime(99, "18:00"); s.setMovieTitle(m.getTitle()); s.setRoomName("Phòng test");
                req.setAttribute("showtime", s); req.setAttribute("showtimes", List.of(s)); req.setAttribute("seats", List.of());
                req.getRequestDispatcher("/" + page + ".jsp").forward(req, res);
            }
        });
        context.addServletMappingDecoded("/qa/jsp", "jspSmoke");
        FilterDef def = new FilterDef(); def.setFilterName("management"); def.setFilter(new ManagementFilter()); context.addFilterDef(def);
        FilterMap map = new FilterMap(); map.setFilterName("management");
        for (String path : List.of("/admin/*", "/booking-history", "/register", "/register.jsp")) map.addURLPattern(path);
        context.addFilterMap(map);
        tomcat.start(); base = "http://localhost:" + tomcat.getConnector().getLocalPort() + "/cinebook";
    }

    @AfterAll void cleanup() throws Exception {
        try { if (tomcat != null) { tomcat.stop(); tomcat.destroy(); } }
        finally {
            if (source != null && database.matches("cinebook_test_[a-f0-9]{32}"))
                source.useHandle(h -> h.execute("DROP DATABASE IF EXISTS `" + database + "`"));
        }
    }

    @Test void registrationAuthorizationAndJspPagesWorkOverHttp() throws Exception {
        HttpClient customer = client();
        assertEquals(302, get(customer, "/admin/dashboard").statusCode());
        HttpResponse<String> register = get(customer, "/register"); assertEquals(200, register.statusCode());
        String csrf = token(register.body());
        assertEquals(403, post(customer, "/register", Map.of("email", "ignored@test.local")).statusCode());
        Map<String,String> fields = new HashMap<>(Map.of("csrfToken", csrf, "fullName", "Nguyễn Test", "email", "new@test.local", "phone", "0901234567", "password", "newsecret", "confirmPassword", "newsecret"));
        fields.put("role", "ADMIN"); // Không được tự nâng quyền thông qua form đăng ký.
        HttpResponse<String> saved = post(customer, "/register", fields); assertEquals(302, saved.statusCode());
        assertTrue(saved.headers().firstValue("location").orElse("").endsWith("/login?registered=1"));
        assertEquals(200, post(customer, "/register", fields).statusCode()); // Email trùng được trả về form.
        assertEquals(Integer.valueOf(1), db.withHandle(h -> h.createQuery("SELECT COUNT(*) FROM users WHERE email='new@test.local'").mapTo(Integer.class).one()));
        assertEquals(302, post(customer, "/login", Map.of("email", "new@test.local", "password", "newsecret")).statusCode());
        assertEquals(403, get(customer, "/admin/movies").statusCode());
        HttpResponse<String> emptyHistory = get(customer, "/booking-history?userId=2");
        assertEquals(200, emptyHistory.statusCode());
        assertFalse(emptyHistory.body().contains("Workflow movie"));
        HttpClient manager = client(); loginAdmin(manager);
        for (String path : List.of("/admin/dashboard", "/admin/movies", "/admin/showtimes", "/admin/bookings")) {
            HttpResponse<String> page = get(manager, path); assertEquals(200, page.statusCode(), path + " " + page.body());
            assertFalse(page.body().contains("Không thể tải dữ liệu"), page.body());
        }
        String token = token(get(manager, "/admin/movies").body());
        HttpResponse<String> movieSaved = post(manager, "/admin/movies", Map.of("csrfToken", token, "action", "save", "id", "0", "title", "HTTP movie", "durationMinutes", "100", "ageRating", "P", "status", "NOW_SHOWING", "genreIds", "1"));
        assertEquals(302, movieSaved.statusCode(), movieSaved.body());
        Movie movie = admin.getMovies().stream().filter(m -> "HTTP movie".equals(m.getTitle())).findFirst().orElseThrow();
        assertEquals(200, get(manager, "/admin/movies?edit=" + movie.getId()).statusCode());
        assertEquals(List.of(1), admin.getMovieGenres(movie.getId()));
        assertEquals(403, post(manager, "/admin/movies", Map.of("action", "delete", "id", "" + movie.getId())).statusCode());
        for (String page : List.of("movies", "movie-detail", "showtimes", "booking")) {
            HttpResponse<String> rendered = get(manager, "/qa/jsp?page=" + page);
            assertEquals(200, rendered.statusCode(), rendered.body());
            assertTrue(rendered.body().contains("&lt;script&gt;"), rendered.body());
            assertFalse(rendered.body().contains("<script>alert('test')</script>"));
        }
    }

    @Test void showtimeConstraintsCounterPaymentCancellationAndHistory() throws Exception {
        Movie movie = movie("Workflow movie"); admin.saveMovie(movie, List.of(1));
        Showtime s = showtime(movie.getId(), "18:00"); admin.saveShowtime(s);
        int showtime = admin.getShowtimes().stream().filter(x -> x.getMovieId() == movie.getId()).findFirst().orElseThrow().getId();
        s.setId(showtime);
        Showtime collision = showtime(movie.getId(), "18:30");
        assertThrows(IllegalArgumentException.class, () -> admin.saveShowtime(collision));
        assertThrows(IllegalArgumentException.class, () -> admin.deleteMovie(movie.getId()));
        int first = new BookingDAO(db).createBooking(2, showtime, List.of(1)); payments.payAtCounter(first);
        HttpClient payer = client();
        assertEquals(302, post(payer, "/login", Map.of("email", "customer@test.local", "password", "customer123")).statusCode());
        assertEquals(200, get(payer, "/payment?bookingId=" + first).statusCode());
        assertThrows(IllegalArgumentException.class, () -> admin.deleteShowtime(showtime));
        s.setPrice(1); assertThrows(IllegalArgumentException.class, () -> admin.saveShowtime(s)); s.setPrice(90000);
        s.setStatus("CANCELLED"); assertThrows(IllegalArgumentException.class, () -> admin.saveShowtime(s)); s.setStatus("CLOSED"); admin.saveShowtime(s);
        s.setStatus("OPEN"); admin.saveShowtime(s);
        HttpClient manager = client(); loginAdmin(manager); String csrf = token(get(manager, "/admin/bookings").body());
        assertEquals(302, post(manager, "/admin/bookings", Map.of("csrfToken", csrf, "action", "confirm", "id", "" + first)).statusCode());
        assertEquals("PAID", payments.findPaymentInfo(first).getPaymentStatus());
        assertThrows(IllegalArgumentException.class, () -> bookings.updateBooking(first, "cancel"));
        int second = new BookingDAO(db).createBooking(2, showtime, List.of(2)); payments.createVnpayPendingPayment(second, "test_ref");
        bookings.updateBooking(second, "cancel");
        assertThrows(IllegalArgumentException.class, () -> payments.processVnpayReturn(second, "test_ref", java.math.BigDecimal.valueOf(9000000), "code", true));
        assertEquals(Integer.valueOf(0), db.withHandle(h -> h.createQuery("SELECT COUNT(*) FROM booking_seats WHERE booking_id=:id").bind("id", second).mapTo(Integer.class).one()));
        assertEquals(2, bookings.findBookings(2, "", "").size()); assertEquals(0, bookings.findBookings(1, "", "").size());
        int expired = new BookingDAO(db).createBooking(2, showtime, List.of(2));
        db.useHandle(h -> h.createUpdate("UPDATE bookings SET hold_expires_at=DATE_SUB(NOW(), INTERVAL 1 MINUTE) WHERE id=:id").bind("id", expired).execute());
        assertEquals("CANCELLED", payments.findPaymentInfo(expired).getBookingStatus());
        assertThrows(IllegalArgumentException.class, () -> payments.payAtCounter(expired));
        HttpClient customer = client(); assertEquals(302, post(customer, "/login", Map.of("email", "customer@test.local", "password", "customer123")).statusCode());
        HttpResponse<String> history = get(customer, "/booking-history?userId=1"); assertEquals(200, history.statusCode(), history.body());
        assertTrue(history.body().contains("Workflow movie"));
        HttpResponse<String> result = get(customer, "/payment-result?bookingId=" + second); assertEquals(200, result.statusCode());
        assertTrue(result.body().contains("Đơn đặt vé đã hủy hoặc hết hạn"));
        int online = new BookingDAO(db).createBooking(2, showtime, List.of(2));
        payments.createVnpayPendingPayment(online, "online_ref");
        assertThrows(IllegalArgumentException.class, () -> payments.processVnpayReturn(online, "wrong_ref", java.math.BigDecimal.valueOf(9000000), "code", true));
        assertThrows(IllegalArgumentException.class, () -> payments.processVnpayReturn(online, "online_ref", java.math.BigDecimal.ONE, "code", true));
        payments.processVnpayReturn(online, "online_ref", java.math.BigDecimal.valueOf(9000000), "online_code", true);
        assertEquals("PAID", payments.findPaymentInfo(online).getPaymentStatus());
        assertThrows(IllegalArgumentException.class, () -> payments.processVnpayReturn(online, "online_ref", java.math.BigDecimal.valueOf(9000000), "online_code", true));
        assertEquals(200, get(manager, "/admin/showtimes?edit=" + showtime).statusCode());
        Movie other = movie("Deletable movie"); admin.saveMovie(other, List.of()); admin.deleteMovie(other.getId());
    }

    private Movie movie(String title) { Movie m = new Movie(); m.setTitle(title); m.setDurationMinutes(120); m.setAgeRating("P"); m.setStatus("NOW_SHOWING"); return m; }
    private Showtime showtime(int movie, String time) { Showtime s = new Showtime(); s.setMovieId(movie); s.setRoomId(1); s.setShowDate(LocalDate.now().plusDays(30).toString()); s.setShowTime(time); s.setPrice(90000); s.setStatus("OPEN"); return s; }
    private HttpClient client() { return HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL)).build(); }
    private void loginAdmin(HttpClient client) throws Exception { assertEquals(302, post(client, "/login", Map.of("email", "admin@test.local", "password", "admin123")).statusCode()); }
    private HttpResponse<String> get(HttpClient client, String path) throws Exception { return client.send(HttpRequest.newBuilder(URI.create(base + path)).GET().build(), HttpResponse.BodyHandlers.ofString()); }
    private HttpResponse<String> post(HttpClient client, String path, Map<String,String> fields) throws Exception {
        String body = fields.entrySet().stream().map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)).collect(java.util.stream.Collectors.joining("&"));
        return client.send(HttpRequest.newBuilder(URI.create(base + path)).header("Content-Type", "application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
    private String token(String html) { var m = Pattern.compile("name=\"csrfToken\" value=\"([^\"]+)\"").matcher(html); assertTrue(m.find(), html); return m.group(1); }
}
