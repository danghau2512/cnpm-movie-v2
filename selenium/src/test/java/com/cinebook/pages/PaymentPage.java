package com.cinebook.pages;

import com.cinebook.pages.AdminShowtimesPage.Show;
import com.cinebook.pages.BookingPage.Seat;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public final class PaymentPage extends UiPage {
    public PaymentPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void assertOrderDetails(int bookingId, Show show, Seat seat) {
        assertEquals(baseUrl + "/payment?bookingId=" + bookingId, driver.getCurrentUrl());
        assertTrue(text().contains(show.title()) && text().contains(show.room()) && text().contains(seat.code()));
        assertTrue(text().contains("80.000"), "Payment must show 80,000 VND");
    }

    public String payAtCounter(int bookingId, Show show, Seat seat) {
        assertEquals(baseUrl + "/payment?bookingId=" + bookingId, driver.getCurrentUrl());
        assertTrue(text().contains(show.title()) && text().contains(seat.code()) && text().contains("80.000"));
        click(By.cssSelector("input[value=PAY_AT_COUNTER]"));
        click(By.cssSelector("form[action$='/payment'] button[type=submit]"));
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/payment-result?bookingId=" + bookingId));
        assertEquals("Đặt vé thành công", visible(By.tagName("h1")).getText());
        assertTrue(text().contains("Chờ thanh toán") && text().contains("Chưa thanh toán"),
                "Choosing counter payment must not mark the order paid");
        String code = driver.findElements(By.cssSelector(".ticket-info strong")).stream().map(WebElement::getText)
                .filter(value -> value.matches("BK[0-9]+" )).findFirst()
                .orElseThrow(() -> new AssertionError("Booking code not displayed"));
        return code;
    }

    public void assertHistory(int bookingId, String code, String bookingStatus, String paymentStatus) {
        open("/booking-history");
        WebElement row = visible(By.cssSelector("a[href$='/payment?bookingId=" + bookingId + "'] , a[href$='/payment-result?bookingId=" + bookingId + "']"))
                .findElement(By.xpath("ancestor::tr"));
        assertTrue(row.getText().contains(code));
        assertTrue(row.getText().contains(bookingStatus) && row.getText().contains(paymentStatus), row.getText());
    }
}
