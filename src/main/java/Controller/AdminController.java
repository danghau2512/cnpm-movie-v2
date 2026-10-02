package Controller;

import Model.Movie;
import Model.Showtime;
import Service.AdminService;
import Service.BookingManagementService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/dashboard", "/admin/movies", "/admin/showtimes", "/admin/bookings"})
public class AdminController extends HttpServlet {
    private final AdminService admin;
    private final BookingManagementService bookings;
    public AdminController() { this(new AdminService(), new BookingManagementService()); }
    public AdminController(AdminService admin, BookingManagementService bookings) { this.admin = admin; this.bookings = bookings; }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try { render(req, res); }
        catch (IllegalArgumentException e) { res.sendError(400, e.getMessage()); }
        catch (RuntimeException e) {
            log("Admin data load failed", e);
            req.setAttribute("error", "Không thể tải dữ liệu. Vui lòng thử lại.");
            req.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(req, res);
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();
        if ("/admin/dashboard".equals(path)) { res.sendError(405); return; }
        try {
            String action = req.getParameter("action");
            if ("/admin/bookings".equals(path)) {
                bookings.updateBooking(number(req.getParameter("id")), action);
            } else if ("delete".equals(action)) {
                int id = number(req.getParameter("id"));
                if ("/admin/movies".equals(path)) admin.deleteMovie(id); else admin.deleteShowtime(id);
            } else if ("save".equals(action)) {
                int id = number(req.getParameter("id"));
                if ("/admin/movies".equals(path)) {
                    Movie movie = new Movie(); movie.setId(id);
                    movie.setTitle(req.getParameter("title")); movie.setDurationMinutes(number(req.getParameter("durationMinutes")));
                    movie.setAgeRating(req.getParameter("ageRating")); movie.setShortDescription(req.getParameter("shortDescription"));
                    movie.setDescription(req.getParameter("description")); movie.setPosterUrl(req.getParameter("posterUrl"));
                    movie.setTrailerUrl(req.getParameter("trailerUrl")); movie.setReleaseDate(req.getParameter("releaseDate"));
                    movie.setStatus(req.getParameter("status"));
                    req.setAttribute("editingMovie", movie);
                    List<Integer> genres = new ArrayList<>();
                    String[] values = req.getParameterValues("genreIds");
                    if (values != null) for (String value : values) genres.add(number(value));
                    req.setAttribute("selectedGenres", genres);
                    admin.saveMovie(movie, genres);
                } else {
                    Showtime s = new Showtime(); s.setId(id); s.setMovieId(number(req.getParameter("movieId")));
                    s.setRoomId(number(req.getParameter("roomId"))); s.setShowDate(req.getParameter("showDate"));
                    s.setShowTime(req.getParameter("showTime")); s.setStatus(req.getParameter("status"));
                    try { s.setPrice(Long.parseLong(req.getParameter("price"))); }
                    catch (NumberFormatException e) { throw new IllegalArgumentException("Giá vé không hợp lệ."); }
                    req.setAttribute("editingShowtime", s);
                    admin.saveShowtime(s);
                }
            } else throw new IllegalArgumentException("Thao tác không hợp lệ.");
            req.getSession().setAttribute("adminMessage", "Thao tác thành công.");
            res.sendRedirect(req.getContextPath() + path);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage()); doGet(req, res);
        } catch (RuntimeException e) {
            log("Admin mutation failed", e);
            req.setAttribute("error", "Không thể lưu thay đổi. Vui lòng thử lại."); doGet(req, res);
        }
    }

    private void render(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String page = req.getServletPath().substring("/admin/".length());
        Object message = req.getSession().getAttribute("adminMessage");
        req.getSession().removeAttribute("adminMessage"); req.setAttribute("message", message);
        if ("movies".equals(page)) {
            List<Movie> movies = admin.getMovies(); req.setAttribute("movies", movies); req.setAttribute("genres", admin.getGenres());
            if ("GET".equals(req.getMethod()) && req.getParameter("edit") != null) {
                int id = number(req.getParameter("edit"));
                req.setAttribute("editingMovie", movies.stream().filter(m -> m.getId() == id).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại.")));
                req.setAttribute("selectedGenres", admin.getMovieGenres(id));
            }
        } else if ("showtimes".equals(page)) {
            List<Showtime> showtimes = admin.getShowtimes(); req.setAttribute("showtimes", showtimes);
            req.setAttribute("movies", admin.getMovies()); req.setAttribute("rooms", admin.getRooms());
            if ("GET".equals(req.getMethod()) && req.getParameter("edit") != null) {
                int id = number(req.getParameter("edit"));
                req.setAttribute("editingShowtime", showtimes.stream().filter(s -> s.getId() == id).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Lịch chiếu không tồn tại.")));
            }
        } else if ("bookings".equals(page)) {
            req.setAttribute("bookings", bookings.getBookings(req.getParameter("status"), req.getParameter("keyword")));
        } else {
            req.setAttribute("movieCount", admin.getMovies().size()); req.setAttribute("showtimeCount", admin.getShowtimes().size());
            req.setAttribute("bookingCount", bookings.getBookings("", "").size());
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/" + page + ".jsp").forward(req, res);
    }
    private int number(String value) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Mã dữ liệu hoặc số nhập không hợp lệ."); }
    }
}
