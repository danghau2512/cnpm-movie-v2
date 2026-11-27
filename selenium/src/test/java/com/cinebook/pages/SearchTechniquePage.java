package com.cinebook.pages;

import org.openqa.selenium.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public final class SearchTechniquePage extends UiPage {
    private static final String PHI = "Phí Phông: Quỷ Máu Rừng Thiêng";
    public SearchTechniquePage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void submitKeyword(String keyword) {
        open("/search");
        WebElement input = visible(By.cssSelector(".header-search input[name=keyword]"));
        input.clear();
        input.sendKeys(keyword);
        assertEquals(keyword, input.getDomProperty("value"), "Representative input must be exact, including spaces");
        WebElement submit = visible(By.cssSelector(".header-search button[type=submit]"));
        click(submit);
        awaitDetached(submit);
        assertTrue(driver.getCurrentUrl().contains("keyword="), "Empty keyword must be sent, not omitted");
        assertFalse(text().contains("HTTP Status 500"));
        for (String filter : List.of("genreFilter", "ratingFilter", "statusFilter"))
            assertEquals("", visible(By.id(filter)).getDomProperty("value"));
        WebElement result = cards().isEmpty() ? visible(By.cssSelector(".empty-message"))
                : cards().get(0).findElement(By.cssSelector(".movie-info"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", result);
    }

    private List<WebElement> cards() { return driver.findElements(By.cssSelector("[data-movie-card]")); }
    private void count(int n) {
        assertEquals(n, cards().size());
        assertTrue(visible(By.cssSelector(".movie-count")).getText().contains("Có " + n + " phim"));
    }

    public void assertTitleMatch() {
        count(1);
        WebElement card = cards().get(0);
        assertEquals(PHI, card.findElement(By.cssSelector(".movie-info h3")).getText());
        for (String value : List.of("120 phút", "16+", "Drama", "Family", "Horror"))
            assertTrue(card.getText().contains(value), "Missing card field: " + value);
        assertTrue(card.findElement(By.cssSelector(".status-badge")).getText().equalsIgnoreCase("Đang chiếu"),
                "Status label must mean Đang chiếu, including its CSS uppercase rendering");
        assertEquals("NOW_SHOWING", card.getDomAttribute("data-status"));
        assertTrue(visible(By.tagName("h1")).getText().contains("Phí Phông"));
        assertTrue(driver.findElements(By.cssSelector(".genre-suggestions")).isEmpty());
    }

    public void assertGenreMatch() {
        count(2);
        Set<String> titles = cards().stream().map(e -> e.findElement(By.cssSelector(".movie-info h3")).getText()).collect(Collectors.toSet());
        assertEquals(Set.of(PHI, "Đại Tiệc Trăng Máu 8"), titles, "Expected the two Horror movies specified in F02_Search_003");
        for (WebElement card : cards()) assertTrue(card.getDomAttribute("data-genre").contains("Horror"));
    }

    public void assertNoMatch() {
        count(0);
        String message = visible(By.cssSelector(".empty-message")).getText();
        assertTrue(message.contains("Không tìm thấy phim phù hợp") && message.contains("ZZZKHONGCOPHIM987"));
        String suggestions = visible(By.cssSelector(".genre-suggestions")).getText();
        for (String genre : List.of("Action", "Drama", "Family", "Horror", "Romance")) assertTrue(suggestions.contains(genre));
    }

    public void assertInvalidKeyword() {
        count(0);
        assertEquals("Vui lòng nhập từ khóa tìm kiếm phim.", visible(By.cssSelector(".empty-message")).getText());
        assertTrue(driver.findElements(By.cssSelector(".genre-suggestions")).isEmpty());
    }
}
