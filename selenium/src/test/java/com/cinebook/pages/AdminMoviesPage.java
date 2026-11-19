package com.cinebook.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public final class AdminMoviesPage extends UiPage {
    public AdminMoviesPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public int create(String title) {
        open("/admin/movies");
        fill("title", title);
        fill("durationMinutes", "120");
        fill("ageRating", "16+");
        fill("shortDescription", "Selenium main flow - test data");
        fill("description", "Created by the main flow Selenium suite.");
        select("status", "NOW_SHOWING");
        WebElement actionGenre = visible(By.xpath("//fieldset//label[normalize-space(.)='Action']/input"));
        click(actionGenre);
        WebElement submit = visible(By.cssSelector(".management-form button[type=submit]"));
        click(submit);
        awaitDetached(submit);
        success();
        List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr[td[2][normalize-space(.)=" + xpathText(title) + "]]"));
        assertEquals(1, rows.size(), "Expected one new movie");
        String href = rows.get(0).findElement(By.cssSelector("a[href*='?edit=']")).getDomAttribute("href");
        int id = Integer.parseInt(href.substring(href.lastIndexOf('=') + 1));
        open("/admin/movies?edit=" + id);
        assertEquals(title, visible(By.name("title")).getDomProperty("value"));
        assertEquals("120", visible(By.name("durationMinutes")).getDomProperty("value"));
        assertEquals("16+", visible(By.name("ageRating")).getDomProperty("value"));
        assertEquals("NOW_SHOWING", visible(By.name("status")).getDomProperty("value"));
        assertTrue(visible(By.xpath("//fieldset//label[normalize-space(.)='Action']/input")).isSelected());
        return id;
    }
}
