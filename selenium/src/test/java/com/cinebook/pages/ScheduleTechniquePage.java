package com.cinebook.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public final class ScheduleTechniquePage extends UiPage {
    public ScheduleTechniquePage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void filter(int movie, LocalDate date, String genre) {
        open("/showtimes");
        select("movieId", "" + movie);
        select("genreName", genre);
        dateOrTime("showDate", date == null ? "" : date.toString());
        WebElement submit = visible(By.cssSelector(".showtime-filter button[type=submit]"));
        click(submit);
        awaitDetached(submit);
        assertFalse(text().contains("HTTP Status 500"));
        assertEquals("" + movie, visible(By.name("movieId")).getDomProperty("value"));
        assertEquals(genre, visible(By.name("genreName")).getDomProperty("value"));
        assertEquals(date == null ? "" : date.toString(), visible(By.name("showDate")).getDomProperty("value"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});",
                visible(By.cssSelector(".showtime-result-summary")));
    }

    public List<Integer> ids() {
        return driver.findElements(By.cssSelector(".showtime-card a[href*='showtimeId=']")).stream()
                .map(e -> Integer.parseInt(e.getDomAttribute("href").split("showtimeId=")[1])).toList();
    }

    public void assertIds(Integer... expected) {
        assertEquals(List.of(expected), ids(), "Wrong schedule IDs/order or duplicate results");
        assertEquals(expected.length, driver.findElements(By.cssSelector(".showtime-card")).size());
        if (expected.length == 0) {
            assertTrue(visible(By.cssSelector(".empty-showtime")).getText().contains("Chưa có lịch chiếu phù hợp"));
            assertTrue(visible(By.cssSelector(".showtime-result-summary")).getText().contains("Không tìm thấy lịch chiếu phù hợp"));
            assertTrue(visible(By.linkText("Xóa bộ lọc")).isDisplayed());
        } else assertTrue(visible(By.cssSelector(".showtime-result-summary")).getText().contains("" + expected.length));
    }

    public void assertCard(int id, LocalDate date, String time) {
        WebElement link = visible(By.cssSelector(".showtime-card a[href$='showtimeId=" + id + "']"));
        WebElement card = link.findElement(By.xpath("ancestor::div[contains(@class,'showtime-card')][1]"));
        for (String value : List.of("F03_TEST_Co_lich", "Room 1", time, date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
            assertTrue(card.getText().contains(value), "Missing schedule field: " + value);
        String price = card.findElements(By.cssSelector(".showtime-info")).get(2).getText().replaceAll("[^0-9]", "");
        assertEquals("80000", price);
    }

    public String bookingUrl(int id) {
        return visible(By.cssSelector(".showtime-card a[href$='showtimeId=" + id + "']")).getDomProperty("href");
    }

    public void assertStaleUrlRejected(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/showtimes"));
        assertTrue(driver.findElements(By.id("bookingForm")).isEmpty());
    }
}
