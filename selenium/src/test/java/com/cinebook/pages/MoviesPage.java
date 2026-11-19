package com.cinebook.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public final class MoviesPage extends UiPage {
    public MoviesPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void searchAndOpenSchedule(String title, int movieId) {
        search(title);
        openSchedule(movieId);
    }

    public void search(String title) {
        open("/home");
        fill("keyword", title);
        WebElement submit = visible(By.cssSelector(".header-search button[type=submit]"));
        click(submit);
        awaitDetached(submit);
        var cards = driver.findElements(By.cssSelector("[data-movie-card]"));
        assertEquals(1, cards.size(), "Search must return the unique test movie");
        String card = cards.get(0).getText();
        for (String value : new String[]{title, "120 phút", "16+", "Action"}) assertTrue(card.contains(value), card);
        assertEquals("NOW_SHOWING", cards.get(0).getDomAttribute("data-status"));
    }

    public void openSchedule(int movieId) {
        click(By.cssSelector("[data-movie-card] a[href$='/showtimes?movieId=" + movieId + "']"));
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/showtimes?movieId=" + movieId));
    }
}
