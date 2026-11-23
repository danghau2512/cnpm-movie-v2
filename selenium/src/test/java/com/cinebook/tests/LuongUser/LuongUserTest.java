package com.cinebook.tests.LuongUser;

import com.cinebook.base.BaseTest;
import com.cinebook.pages.*;
import com.cinebook.pages.AdminShowtimesPage.Show;
import com.cinebook.pages.BookingPage.Seat;
import com.cinebook.utils.FlowEvidence;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LuongUserTest extends BaseTest {
    private Show show;
    private DemoPage page;
    private FlowEvidence evidence;
    private long started;

    @BeforeEach
    void chuanBiSuatChieu() {
        // Chuẩn bị bằng phiên Admin ẩn, không chen vào luồng khách đang demo.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver admin = new ChromeDriver(options);
        try {
            admin.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            new LoginPage(admin, baseUrl).login(config.account("admin"), true);
            show = new AdminShowtimesPage(admin, baseUrl).create(2,
                    "Phí Phông: Quỷ Máu Rừng Thiêng", config.get("flow.roomId", "1"), 14);
        } finally {
            admin.quit();
        }
        page = new DemoPage(driver, baseUrl);
        evidence = new FlowEvidence(driver, "LuongUser", true);
        page.loginForm();
    }

    @Test
    @DisplayName("Khách đăng nhập → tìm/lọc phim → chọn suất/ghế → thanh toán tại quầy")
    void dangNhapDenThanhToan() {
        started = System.nanoTime();
        page.credentials(config.account("customer").email(), config.account("customer").password());
        xem(3, "01-Thong-tin-dang-nhap");
        page.submitLogin();
        xem(6, "02-Dang-nhap-thanh-cong");
        page.movies();
        xem(9, "03-Danh-sach-phim");
        page.search();
        xem(12, "04-Tim-kiem-theo-ten");
        page.filter("genreFilter", "Horror", "data-genre");
        xem(15, "05-Loc-the-loai");
        page.filter("ratingFilter", "16+", "data-rating");
        xem(18, "06-Loc-do-tuoi");
        page.filter("statusFilter", "NOW_SHOWING", "data-status");
        xem(21, "07-Loc-dang-chieu");
        page.details();
        xem(25, "08-Chi-tiet-phim");
        page.schedule(show);
        xem(29, "09-Loc-phim-the-loai-ngay-chieu");
        page.book(show);
        BookingPage booking = new BookingPage(driver, baseUrl);
        Seat seat = booking.selectFirstAvailable(show);
        xem(33, "10-Chon-ghe-va-tong-tien");
        int bookingId = booking.submit();
        PaymentPage payment = new PaymentPage(driver, baseUrl);
        payment.assertOrderDetails(bookingId, show, seat);
        page.counter();
        xem(37, "11-Chon-thanh-toan-tai-quay");
        String code = payment.payAtCounter(bookingId, show, seat);
        xem(41, "12-Dat-ve-thanh-cong");
        payment.assertHistory(bookingId, code, "Chờ thanh toán", "Chưa thanh toán");
        xem(44, "13-Lich-su-dat-ve");
        System.out.printf("LuongUser: %.1f giây (không gồm chuẩn bị suất/khởi động Chrome).%n",
                (System.nanoTime() - started) / 1_000_000_000.0);
    }

    private void xem(int second, String step) {
        // Chờ tới mốc trên toàn luồng; thời gian tải trang được tính vào 44 giây.
        long remaining = Duration.ofSeconds(second).toNanos() - (System.nanoTime() - started);
        if (remaining > 0) {
            try { Thread.sleep(Duration.ofNanos(remaining).toMillis()); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException(e); }
        }
        evidence.step(step, step, () -> assertFalse(driver.findElement(By.tagName("body")).getText().contains("HTTP Status 500")));
    }

    private static class DemoPage extends UiPage {
        DemoPage(WebDriver driver, String url) { super(driver, url); }
        void loginForm() { open("/login"); visible(By.id("loginForm")); }
        void credentials(String email, String password) { fill("email", email); fill("password", password); }
        void submitLogin() {
            click(By.cssSelector("#loginForm button[type=submit]"));
            wait.until(ExpectedConditions.urlToBe(baseUrl + "/home"));
        }
        void movies() {
            click(By.cssSelector("header a[href$='/movies']"));
            visible(By.id("genreFilter"));
            assertFalse(cards().isEmpty());
        }
        void search() {
            fill("keyword", "Phí Phông");
            WebElement button = visible(By.cssSelector(".header-search button[type=submit]"));
            click(button);
            awaitDetached(button);
            assertEquals(1, cards().size());
            assertTrue(cards().get(0).getText().contains("Phí Phông: Quỷ Máu Rừng Thiêng"));
        }
        List<WebElement> cards() {
            return driver.findElements(By.cssSelector("[data-movie-card]")).stream().filter(WebElement::isDisplayed).toList();
        }
        void filter(String id, String value, String attribute) {
            WebElement select = visible(By.id(id));
            new Select(select).selectByValue(value);
            assertFalse(cards().isEmpty());
            for (WebElement card : cards()) assertTrue(card.getDomAttribute(attribute).contains(value));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", select);
        }
        void details() {
            click(By.cssSelector("[data-movie-card] a[href*='/movie-detail?id=2']"));
            wait.until(ExpectedConditions.urlContains("/movie-detail?id=2"));
            assertEquals("Phí Phông: Quỷ Máu Rừng Thiêng", visible(By.tagName("h1")).getText());
        }
        void schedule(Show show) {
            click(By.cssSelector("a[href$='/showtimes?movieId=2']"));
            wait.until(ExpectedConditions.urlContains("/showtimes?movieId=2"));
            assertEquals("2", visible(By.name("movieId")).getDomProperty("value"));
            select("genreName", "Horror");
            dateOrTime("showDate", show.date().toString());
            WebElement submit = visible(By.cssSelector(".showtime-filter button[type=submit]"));
            click(submit);
            awaitDetached(submit);
            assertEquals(1, driver.findElements(By.cssSelector(".showtime-card")).size());
            WebElement card = visible(By.cssSelector(".showtime-card"));
            for (String value : List.of(show.title(), show.room(), show.time())) assertTrue(card.getText().contains(value));
        }
        void book(Show show) {
            click(By.cssSelector(".showtime-card a[href$='showtimeId=" + show.id() + "']"));
            wait.until(ExpectedConditions.urlToBe(baseUrl + "/booking?showtimeId=" + show.id()));
            visible(By.id("bookingForm"));
        }
        void counter() { click(By.cssSelector("input[value=PAY_AT_COUNTER]")); }
    }
}
