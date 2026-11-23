package com.cinebook.pages;

import org.openqa.selenium.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class AdminTechniquePage extends UiPage {
    public AdminTechniquePage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void movieForm(String title, int duration) {
        open("/admin/movies");
        fill("title", title);
        fill("durationMinutes", "" + duration);
        fill("ageRating", "16+");
        fill("shortDescription", "Phim kiểm thử");
        fill("description", "Dữ liệu thử cho giá trị biên thời lượng.");
        select("status", "NOW_SHOWING");
        click(By.xpath("//fieldset//label[normalize-space(.)='Action']/input"));
    }

    public void saveMovie(boolean valid, int duration) {
        WebElement button = visible(By.cssSelector(".management-form button[type=submit]"));
        if (!valid) {
            WebElement input = visible(By.name("durationMinutes"));
            assertEquals("1", input.getDomAttribute("min"));
            assertEquals("600", input.getDomAttribute("max"));
            click(button);
            assertEquals(Boolean.FALSE, ((JavascriptExecutor)driver).executeScript("return arguments[0].checkValidity()", input));
            assertEquals("" + duration, input.getDomProperty("value"));
        } else {
            click(button);
            awaitDetached(button);
            success();
        }
    }

    public void assertMovieReadback(int id, String title, int duration) {
        open("/admin/movies?edit=" + id);
        assertEquals(title, visible(By.name("title")).getDomProperty("value"));
        assertEquals("" + duration, visible(By.name("durationMinutes")).getDomProperty("value"));
        assertEquals("NOW_SHOWING", visible(By.name("status")).getDomProperty("value"));
        assertTrue(visible(By.xpath("//fieldset//label[normalize-space(.)='Action']/input")).isSelected());
    }

    public void showtime(int movie, int room, LocalDate date, String time, boolean valid) {
        open("/admin/showtimes");
        select("movieId", "" + movie);
        select("roomId", "" + room);
        dateOrTime("showDate", date.toString());
        dateOrTime("showTime", time);
        fill("price", "90000");
        select("status", "OPEN");
        WebElement button = visible(By.cssSelector(".management-form button"));
        click(button);
        awaitDetached(button);
        if (valid) success();
        else assertFalse(visible(By.cssSelector(".notice.error")).getText().isBlank());
    }

    public String error() { return visible(By.cssSelector(".notice.error")).getText(); }
    public void changeMovieStatus(int id, String status) {
        open("/admin/movies?edit=" + id);
        select("status", status);
        WebElement button = visible(By.cssSelector(".management-form button[type=submit]"));
        click(button); awaitDetached(button); success();
    }
}
