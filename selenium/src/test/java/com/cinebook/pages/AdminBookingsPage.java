package com.cinebook.pages;

import com.cinebook.pages.BookingPage.Seat;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public final class AdminBookingsPage extends UiPage {
    public AdminBookingsPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void find(String code) {
        open("/admin/bookings");
        WebElement keyword = visible(By.cssSelector(".filter-form input[name=keyword]"));
        keyword.clear();
        keyword.sendKeys(code);
        select("status", "");
        WebElement button = visible(By.cssSelector(".filter-form button"));
        click(button);
        awaitDetached(button);
        assertEquals(1, driver.findElements(By.cssSelector("tbody tr td:first-child strong")).size());
        row(code);
    }

    private WebElement row(String code) { return visible(By.xpath("//tr[td/strong[text()=" + xpathText(code) + "]]")); }

    public void confirm(String code, Seat seat) {
        find(code);
        assertTrue(row(code).getText().contains("Chờ thanh toán") && row(code).getText().contains("Tại quầy"));
        act(code, "confirm");
        find(code);
        String text = row(code).getText();
        assertTrue(text.contains("Đã xác nhận") && text.contains("Đã thanh toán") && text.contains(seat.code()), text);
        assertTrue(row(code).findElements(By.tagName("button")).isEmpty());
    }

    public void cancel(String code) {
        find(code);
        assertTrue(row(code).getText().contains("Chờ thanh toán"));
        act(code, "cancel");
        find(code);
        String text = row(code).getText();
        assertTrue(text.contains("Đã hủy") && text.contains("Thanh toán thất bại"), text);
        assertTrue(row(code).findElements(By.tagName("button")).isEmpty());
    }

    private void act(String code, String action) {
        WebElement button = row(code).findElement(By.cssSelector("button[value='" + action + "']"));
        click(button);
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        awaitDetached(button);
        success();
    }
}
