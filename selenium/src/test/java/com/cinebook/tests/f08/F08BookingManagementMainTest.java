package com.cinebook.tests.f08;

import com.cinebook.base.FeatureMainTest;
import com.cinebook.pages.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class F08BookingManagementMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F08 main | Admin confirms payment at counter")
    void confirmCounterPayment() {
        OrderData order = prepare("Create a pending counter order; switch to Admin", () -> {
            OrderData data = createCounterOrder(); loginAs("admin"); return data;
        });
        evidence.step("F08-confirm", "Find code and confirm payment; reference F08_Booking_010", () ->
                new AdminBookingsPage(driver, baseUrl).confirm(order.code(), order.seat()));
        evidence.step("F08-customer-result", "Customer sees CONFIRMED/PAID", () -> {
            loginAs("customer");
            new PaymentPage(driver, baseUrl).assertHistory(order.id(), order.code(), "Đã xác nhận", "Đã thanh toán");
        });
        evidence.step("F08-seat-retained", "Another customer cannot select the paid seat", () -> {
            assertNotEquals(config.account("customer").email(), config.account("otherCustomer").email());
            loginAs("otherCustomer");
            new BookingPage(driver, baseUrl).openAndAssertHeld(order.show(), order.seat());
        });
    }

    @Test
    @DisplayName("F08 main | Admin cancels and another customer rebooks the seat")
    void cancelAndReleaseSeat() {
        OrderData order = prepare("Create a pending counter order; switch to Admin", () -> {
            OrderData data = createCounterOrder(); loginAs("admin"); return data;
        });
        evidence.step("F08-cancel", "Cancel pending order; reference F08_Booking_012", () ->
                new AdminBookingsPage(driver, baseUrl).cancel(order.code()));
        evidence.step("F08-customer-result", "Original customer sees CANCELLED/FAILED", () -> {
            loginAs("customer");
            new PaymentPage(driver, baseUrl).assertHistory(order.id(), order.code(), "Đã hủy", "Thanh toán thất bại");
        });
        evidence.step("F08-released-seat", "Another customer creates a new order for the exact released seat", () -> {
            assertNotEquals(config.account("customer").email(), config.account("otherCustomer").email());
            loginAs("otherCustomer");
            BookingPage booking = new BookingPage(driver, baseUrl);
            booking.openReleasedAndSelect(order.show(), order.seat());
            int id = booking.submit();
            assertNotEquals(order.id(), id);
            new PaymentPage(driver, baseUrl).assertOrderDetails(id, order.show(), order.seat());
        });
    }
}
