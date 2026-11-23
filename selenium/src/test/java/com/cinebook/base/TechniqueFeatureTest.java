package com.cinebook.base;

import com.cinebook.pages.*;
import com.cinebook.pages.AdminShowtimesPage.Show;
import com.cinebook.utils.TestDatabase;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import java.time.LocalDate;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TechniqueFeatureTest extends FeatureMainTest {
    protected TestDatabase db;
    private int pauseMs;

    @BeforeEach
    void prepareTechnique() throws Exception {
        String feature = getClass().getSimpleName().substring(0, 3).toLowerCase(Locale.ROOT);
        baseUrl = config.get(feature + ".baseUrl", baseUrl).replaceAll("/+$", "");
        login = new LoginPage(driver, baseUrl);
        pauseMs = Integer.parseInt(config.get("technique.pauseMs", "2500"));
        assertTrue(pauseMs >= 0 && pauseMs <= 10000);
        db = new TestDatabase(config);
    }

    @AfterEach
    void closeTestDatabase() throws Exception { if (db != null) db.close(); }

    @Override protected MovieData createMovie() {
        MovieData movie = super.createMovie();
        assertEquals(movie.title(), db.value("SELECT title FROM movies WHERE id=?", movie.id()), "Web và cấu hình JDBC phải cùng DB thử");
        return movie;
    }

    @Override protected Show createShow(MovieData movie) {
        Show show = super.createShow(movie);
        assertEquals(movie.id(), db.number("SELECT movie_id FROM showtimes WHERE id=?", show.id()));
        return show;
    }

    @Override protected OrderData createUnpaidOrder() {
        OrderData order = super.createUnpaidOrder();
        db.bookingState(order.id(), "PENDING", "UNPAID");
        assertEquals(1, db.number("SELECT COUNT(*) FROM bookings WHERE id=? AND showtime_id=? AND quantity=1 AND total_amount=80000", order.id(), order.show().id()));
        assertSeatHeld(order);
        return new OrderData(order.show(), order.seat(), order.id(), db.value("SELECT booking_code FROM bookings WHERE id=?", order.id()));
    }

    @Override protected OrderData createCounterOrder() {
        OrderData order = super.createCounterOrder();
        db.bookingState(order.id(), "PENDING", "UNPAID");
        assertPayment(order.id(), "PAY_AT_COUNTER", "PENDING");
        assertEquals(order.code(), db.value("SELECT booking_code FROM bookings WHERE id=?", order.id()));
        return order;
    }

    protected void demonstrate(String tc, String description, Runnable test) {
        evidence.step(tc, description, () -> {
            try { test.run(); }
            finally {
                try { Thread.sleep(pauseMs); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException(e); }
            }
        });
    }

    protected LocalDate freeDate() {
        return LocalDate.parse(db.value("SELECT DATE_FORMAT(DATE_ADD(GREATEST(CURDATE(),COALESCE(MAX(DATE(start_time)),CURDATE())),INTERVAL 3 DAY),'%Y-%m-%d') FROM showtimes"));
    }

    protected void confirmOrder(OrderData order) {
        loginAs("admin");
        new AdminBookingsPage(driver, baseUrl).confirm(order.code(), order.seat());
    }

    protected void assertPayment(int id, String method, String status) {
        assertEquals(1, db.number("SELECT COUNT(*) FROM payments WHERE booking_id=? AND payment_method=? AND payment_status=? AND amount=80000", id, method, status));
        assertEquals(1, db.number("SELECT COUNT(*) FROM payments WHERE booking_id=?", id));
        if (status.equals("SUCCESS")) assertEquals(1, db.number("SELECT COUNT(*) FROM payments WHERE booking_id=? AND paid_at IS NOT NULL", id));
        else assertEquals(1, db.number("SELECT COUNT(*) FROM payments WHERE booking_id=? AND paid_at IS NULL", id));
    }

    protected void assertSeatHeld(OrderData order) {
        assertEquals(1, db.number("SELECT COUNT(*) FROM booking_seats WHERE booking_id=? AND showtime_id=? AND seat_id=?", order.id(), order.show().id(), order.seat().id()));
    }

    protected String body() { return driver.findElement(By.tagName("body")).getText(); }

    protected void browserPost(String path, String... namesAndValues) {
        WebElement previousPage = driver.findElement(By.tagName("body"));
        // Gửi lại mẫu form bằng chính phiên trình duyệt để kiểm tra yêu cầu không thể nhập qua UI.
        ((JavascriptExecutor) driver).executeScript("""
            const f=document.createElement('form');f.method='post';f.action=arguments[0];
            const values=arguments[1];
            for(let i=0;i<values.length;i+=2){const input=document.createElement('input');input.type='hidden';input.name=values[i];input.value=values[i+1];f.appendChild(input);}
            document.body.appendChild(f);f.submit();
            """, baseUrl + path, java.util.Arrays.asList(namesAndValues));
        wait.until(d -> {
            try { previousPage.isEnabled(); return false; }
            catch (StaleElementReferenceException e) { return true; }
            catch (WebDriverException e) {
                if (e.getMessage().contains("Node with given id does not belong to the document")) return true;
                throw e;
            }
        });
        wait.until(d -> "complete".equals(((JavascriptExecutor)d).executeScript("return document.readyState")));
    }
}
