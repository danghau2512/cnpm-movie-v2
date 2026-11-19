package com.cinebook.pages;

import com.cinebook.pages.AdminShowtimesPage.Show;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public final class ShowtimesPage extends UiPage {
    public ShowtimesPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void openForMovie(int movieId) { open("/showtimes?movieId=" + movieId); }

    public void filterAndBook(Show show) {
        assertEquals("" + show.movieId(), visible(By.name("movieId")).getDomProperty("value"));
        dateOrTime("showDate", show.date().toString());
        WebElement submit = visible(By.cssSelector(".showtime-filter button[type=submit]"));
        click(submit);
        awaitDetached(submit);
        assertEquals(1, driver.findElements(By.cssSelector(".showtime-card")).size());
        WebElement link = visible(By.cssSelector(".showtime-card a[href$='showtimeId=" + show.id() + "']"));
        WebElement card = link.findElement(By.xpath("ancestor::div[contains(@class,'showtime-card')][1]"));
        for (String value : new String[]{show.title(), show.room(), show.time()}) assertTrue(card.getText().contains(value));
        click(link);
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/booking?showtimeId=" + show.id()));
        assertTrue(visible(By.id("bookingForm")).isDisplayed());
    }
}
