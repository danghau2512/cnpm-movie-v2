package com.cinebook.pages;

import com.cinebook.pages.AdminShowtimesPage.Show;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public final class BookingPage extends UiPage {
    public record Seat(String id, String code) {}
    public BookingPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void openShow(Show show) {
        open("/booking?showtimeId=" + show.id());
        assertTrue(visible(By.id("bookingForm")).isDisplayed());
        assertEquals("" + show.id(), driver.findElement(By.name("showtimeId")).getDomProperty("value"));
    }

    public Seat selectFirstAvailable(Show show) {
        assertTrue(text().contains(show.title()) && text().contains(show.room()));
        assertEquals("" + show.id(), driver.findElement(By.name("showtimeId")).getDomProperty("value"));
        WebElement seat = driver.findElements(By.cssSelector("input[name=seatIds]:not(:disabled)")).stream().findFirst()
                .orElseThrow(() -> new AssertionError("New showtime has no free seats"));
        Seat result = new Seat(seat.getDomAttribute("value"), seat.getDomAttribute("data-code"));
        select(result, show.price());
        return result;
    }

    public void select(Seat seat, long price) {
        WebElement input = driver.findElement(By.cssSelector("input[name=seatIds][value='" + seat.id() + "']"));
        assertTrue(input.isEnabled(), "Seat must be available: " + seat.code());
        click(input.findElement(By.xpath("following-sibling::span")));
        assertTrue(input.isSelected());
        assertEquals(seat.code(), visible(By.id("selectedSeats")).getText());
        assertEquals("1", visible(By.id("seatCount")).getText());
        assertEquals("" + price, visible(By.id("totalPrice")).getText().replaceAll("[^0-9]", ""));
        assertTrue(visible(By.id("submitBookingBtn")).isEnabled());
    }

    public int submit() {
        click(By.id("submitBookingBtn"));
        wait.until(ExpectedConditions.urlContains("/payment?bookingId="));
        return Integer.parseInt(driver.getCurrentUrl().split("bookingId=")[1]);
    }

    public void openAndAssertHeld(Show show, Seat seat) {
        open("/booking?showtimeId=" + show.id());
        assertFalse(driver.findElement(By.cssSelector("input[name=seatIds][value='" + seat.id() + "']")).isEnabled(),
                "A paid seat must stay unavailable to another customer");
    }

    public void openReleasedAndSelect(Show show, Seat seat) {
        open("/booking?showtimeId=" + show.id());
        select(seat, show.price());
    }
}
