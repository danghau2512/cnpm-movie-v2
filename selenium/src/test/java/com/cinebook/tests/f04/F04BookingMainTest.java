package com.cinebook.tests.f04;

import com.cinebook.base.FeatureMainTest;
import com.cinebook.pages.*;
import com.cinebook.pages.AdminShowtimesPage.Show;
import com.cinebook.pages.BookingPage.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class F04BookingMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F04 main | Select one seat and create an unpaid booking")
    void selectSeatAndCreateOrder() {
        Show show = prepare("Create a future show and open its seat map as Customer", () -> {
            Show data = createShow(); loginAs("customer");
            new BookingPage(driver, baseUrl).openShow(data); return data;
        });
        BookingPage booking = new BookingPage(driver, baseUrl);
        Seat[] seat = new Seat[1];
        evidence.step("F04-seat", "Select one seat, quantity=1, total=80,000; reference F04_Seat_002", () ->
                seat[0] = booking.selectFirstAvailable(show));
        evidence.step("F04-order", "Submit once and verify correct order at payment", () -> {
            int id = booking.submit();
            new PaymentPage(driver, baseUrl).assertOrderDetails(id, show, seat[0]);
        });
    }
}
