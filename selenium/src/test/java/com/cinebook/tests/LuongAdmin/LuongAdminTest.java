package com.cinebook.tests.LuongAdmin;

import com.cinebook.base.BaseTest;
import com.cinebook.pages.*;
import com.cinebook.pages.AdminShowtimesPage.Show;
import com.cinebook.pages.BookingPage.Seat;
import com.cinebook.utils.FlowEvidence;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

public class LuongAdminTest extends BaseTest {
    private record Order(String code, Seat seat) {}
    private Order thuTien, huyDon;
    private DemoAdminPage page;
    private FlowEvidence evidence;
    private long started;

    @BeforeEach
    void chuanBiHaiDonTaiQuay() {
        // Tạo đơn thử qua UI bằng Chrome ẩn trước khi bắt đầu demo Admin.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver customer = new ChromeDriver(options);
        try {
            customer.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            LoginPage login = new LoginPage(customer, baseUrl);
            login.login(config.account("admin"), true);
            String title = tenPhim("Chuyến Tàu Mùa Hạ");
            int movie = new AdminMoviesPage(customer, baseUrl).create(title);
            Show show = new AdminShowtimesPage(customer, baseUrl).create(movie, title, config.get("flow.roomId", "1"), 14);
            login.logout();
            login.login(config.account("customer"), false);
            thuTien = taoDon(customer, show);
            huyDon = taoDon(customer, show);
        } finally { customer.quit(); }
        page = new DemoAdminPage(driver, baseUrl);
        page.loginForm();
        evidence = new FlowEvidence(driver, "LuongAdmin", true);
    }

    private Order taoDon(WebDriver browser, Show show) {
        BookingPage booking = new BookingPage(browser, baseUrl);
        booking.openShow(show);
        new org.openqa.selenium.support.ui.WebDriverWait(browser, Duration.ofSeconds(30))
                .until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
        Seat seat = new DemoAdminPage(browser, baseUrl).selectSeat(show);
        int id = booking.submit();
        return new Order(new PaymentPage(browser, baseUrl).payAtCounter(id, show, seat), seat);
    }

    @Test
    @DisplayName("Admin: tổng quan → thêm/sửa/xóa phim, lịch → tìm/lọc và xử lý đơn → đăng xuất")
    void quanLyPhimLichChieuVaDonVe() {
        started = System.nanoTime();
        page.credentials(config.account("admin").email(), config.account("admin").password());
        xem(5, "01-Nhap-tai-khoan-Admin");
        page.submitLogin();
        xem(10, "02-Tong-quan-quan-tri");

        String title = tenPhim("Hẹn Nhau Ở Đà Lạt");
        page.navigate("movies");
        page.fillMovie(title);
        xem(16, "03-Nhap-phim-va-chon-the-loai");
        int movie = page.saveMovie(title);
        xem(22, "04-Them-phim-thanh-cong");
        page.editMovie(movie, title);
        xem(28, "05-Sua-phim-thanh-cong");

        Show show = new AdminShowtimesPage(driver, baseUrl).create(movie, title, config.get("flow.roomId", "1"), 14);
        page.showSchedule(show.id());
        xem(35, "06-Them-lich-chieu");
        page.editSchedule(show.id());
        xem(41, "07-Sua-gia-va-trang-thai-lich");
        page.deleteSchedule(show.id());
        xem(46, "08-Xoa-lich-thu-chua-co-don");
        page.navigate("movies");
        page.deleteMovie(movie);
        xem(51, "09-Xoa-phim-thu");

        page.navigate("bookings");
        page.findOrder(thuTien.code());
        xem(56, "10-Tim-don-theo-ma");
        page.pending(thuTien.code());
        xem(61, "11-Loc-don-cho-thanh-toan");
        page.confirm(thuTien.code(), thuTien.seat());
        xem(66, "12-Xac-nhan-thu-tien-tai-quay");
        page.cancel(huyDon.code());
        xem(71, "13-Huy-don-thu-con-lai");
        page.logout();
        xem(75, "14-Dang-xuat");
        System.out.printf("LuongAdmin: %.1f giây (không gồm chuẩn bị hai đơn/khởi động Chrome).%n",
                (System.nanoTime() - started) / 1_000_000_000.0);
    }

    private void xem(int second, String step) {
        long remaining = Duration.ofSeconds(second).toNanos() - (System.nanoTime() - started);
        if (remaining > 0) {
            try { Thread.sleep(Duration.ofNanos(remaining).toMillis()); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException(e); }
        }
        evidence.step(step, step, () -> assertFalse(driver.findElement(By.tagName("body")).getText().contains("HTTP Status 500")));
    }

    private static String tenPhim(String title) {
        return title + " (" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm:ss")) + ")";
    }

    private static class DemoAdminPage extends UiPage {
        DemoAdminPage(WebDriver driver, String url) { super(driver, url); }
        void loginForm() { open("/login"); visible(By.id("loginForm")); }
        Seat selectSeat(Show show) {
            assertTrue(text().contains(show.title()));
            WebElement input = driver.findElements(By.cssSelector("input[name=seatIds]:not(:disabled)"))
                    .stream().findFirst().orElseThrow(() -> new AssertionError("Suất thử không còn ghế trống"));
            Seat seat = new Seat(input.getDomAttribute("value"), input.getDomAttribute("data-code"));
            click(input.findElement(By.xpath("..")));
            wait.until(ExpectedConditions.elementToBeSelected(input));
            wait.until(ExpectedConditions.textToBe(By.id("selectedSeats"), seat.code()));
            assertEquals("1", visible(By.id("seatCount")).getText());
            assertEquals("" + show.price(), visible(By.id("totalPrice")).getText().replaceAll("[^0-9]", ""));
            assertTrue(visible(By.id("submitBookingBtn")).isEnabled());
            return seat;
        }
        void credentials(String email, String password) { fill("email", email); fill("password", password); }
        void submitLogin() {
            click(By.cssSelector("#loginForm button[type=submit]"));
            wait.until(ExpectedConditions.urlToBe(baseUrl + "/admin/dashboard"));
            assertEquals("Quản trị CineBook", visible(By.tagName("h1")).getText());
            assertEquals(3, driver.findElements(By.cssSelector(".summary-grid a")).size());
        }
        void navigate(String area) {
            click(By.cssSelector(".management-nav a[href$='/admin/" + area + "']"));
            wait.until(ExpectedConditions.urlToBe(baseUrl + "/admin/" + area));
        }
        void fillMovie(String title) {
            fill("title", title);
            fill("durationMinutes", "120");
            fill("ageRating", "16+");
            fill("shortDescription", "Phim dùng để demo quản lý.");
            fill("description", "Dữ liệu thử riêng cho buổi demo Selenium.");
            select("status", "NOW_SHOWING");
            click(By.xpath("//fieldset//label[normalize-space(.)='Action']/input"));
        }
        void save() {
            WebElement button = visible(By.cssSelector(".management-form button[type=submit], .management-form button.btn-primary"));
            click(button);
            awaitDetached(button);
            success();
        }
        WebElement movieRow(int id) { return visible(By.xpath("//tbody/tr[td[1][normalize-space(.)='" + id + "']]")); }
        int saveMovie(String title) {
            save();
            WebElement row = visible(By.xpath("//tbody/tr[td[2][normalize-space(.)=" + xpathText(title) + "]]"));
            assertTrue(row.getText().contains("120 phút") && row.getText().contains("Đang chiếu"));
            return Integer.parseInt(row.findElement(By.tagName("td")).getText());
        }
        void editMovie(int id, String title) {
            click(movieRow(id).findElement(By.cssSelector("a[href*='?edit=']")));
            fill("durationMinutes", "125");
            fill("shortDescription", "Đã cập nhật nội dung giới thiệu.");
            save();
            assertTrue(movieRow(id).getText().contains(title) && movieRow(id).getText().contains("125 phút"));
        }
        void showSchedule(int id) {
            open("/admin/showtimes");
            scroll(scheduleRow(id));
        }
        WebElement scheduleRow(int id) {
            return visible(By.cssSelector("a[href$='/admin/showtimes?edit=" + id + "']")).findElement(By.xpath("ancestor::tr"));
        }
        void editSchedule(int id) {
            click(scheduleRow(id).findElement(By.cssSelector("a[href*='?edit=']")));
            fill("price", "90000");
            select("status", "CLOSED");
            save();
            WebElement row = scheduleRow(id);
            assertTrue(row.getText().contains("90000") && row.getText().contains("Đóng đặt vé"));
            scroll(row);
        }
        void deleteSchedule(int id) {
            remove(scheduleRow(id));
            assertTrue(driver.findElements(By.cssSelector("a[href$='/admin/showtimes?edit=" + id + "']")).isEmpty());
        }
        void deleteMovie(int id) {
            remove(movieRow(id));
            assertTrue(driver.findElements(By.xpath("//tbody/tr[td[1][normalize-space(.)='" + id + "']]")).isEmpty());
        }
        void remove(WebElement row) {
            WebElement button = row.findElement(By.cssSelector("button[value=delete]"));
            click(button);
            wait.until(ExpectedConditions.alertIsPresent()).accept();
            awaitDetached(button);
            success();
        }
        void findOrder(String code) {
            WebElement keyword = visible(By.cssSelector(".filter-form input[name=keyword]"));
            keyword.clear();
            keyword.sendKeys(code);
            filterOrders();
            assertEquals(1, driver.findElements(By.cssSelector("tbody tr td:first-child strong")).size());
            assertTrue(visible(By.cssSelector("tbody")).getText().contains(code));
        }
        void pending(String code) {
            select("status", "PENDING");
            filterOrders();
            assertEquals("PENDING", visible(By.name("status")).getDomProperty("value"));
            String row = visible(By.cssSelector("tbody")).getText();
            assertTrue(row.contains(code) && row.contains("Chờ thanh toán") && row.contains("Tại quầy"));
        }
        void filterOrders() {
            WebElement button = visible(By.cssSelector(".filter-form button"));
            click(button);
            awaitDetached(button);
        }
        void confirm(String code, Seat seat) { new AdminBookingsPage(driver, baseUrl).confirm(code, seat); }
        void cancel(String code) { new AdminBookingsPage(driver, baseUrl).cancel(code); }
        void logout() {
            click(By.cssSelector("header a[href$='/logout']"));
            open("/login");
            visible(By.id("loginForm"));
            assertTrue(driver.findElements(By.cssSelector("header a[href$='/logout']")).isEmpty());
        }
        void scroll(WebElement element) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        }
    }
}
