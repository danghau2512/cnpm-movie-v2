package com.cinebook.pages;

import org.openqa.selenium.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/** Fixture inspection/state changes through a separate Admin browser, preserving the Customer session. */
public final class AdminScheduleStatePage extends UiPage {
    public record Snapshot(int id, int movie, int room, LocalDate date, String time, long price, String status) {}
    public AdminScheduleStatePage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public Snapshot read(int id) {
        open("/admin/showtimes?edit=" + id);
        assertEquals("" + id, driver.findElement(By.cssSelector(".management-form input[name=id]")).getDomProperty("value"),
                "Verify that the web and Selenium JDBC both use cinebook_design_demo");
        return new Snapshot(id,
                Integer.parseInt(visible(By.name("movieId")).getDomProperty("value")),
                Integer.parseInt(visible(By.name("roomId")).getDomProperty("value")),
                LocalDate.parse(visible(By.name("showDate")).getDomProperty("value")),
                visible(By.name("showTime")).getDomProperty("value"),
                Long.parseLong(visible(By.name("price")).getDomProperty("value")),
                visible(By.name("status")).getDomProperty("value"));
    }

    public void setStatus(int id, String status) {
        Snapshot before = read(id);
        select("status", status);
        WebElement submit = visible(By.cssSelector(".management-form button"));
        click(submit);
        awaitDetached(submit);
        success();
        Snapshot after = read(id);
        assertEquals(new Snapshot(before.id(), before.movie(), before.room(), before.date(), before.time(), before.price(), status), after,
                "Only the fixture status may change");
    }
}
