package com.cinebook.tests.f04;

import com.cinebook.base.TechniqueFeatureTest;
import com.cinebook.pages.*;
import com.cinebook.pages.AdminShowtimesPage.Show;
import com.cinebook.pages.BookingPage.Seat;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F04DecisionTableTest extends TechniqueFeatureTest {
    @Test @Order(1)
    @DisplayName("F04_Seat_002 - R1: Suất mở, ghế đúng phòng và trống")
    void datGheHopLe() {
        Show show = prepare("Chuẩn bị suất và đăng nhập khách", () -> { Show s=createShow(); loginAs("customer"); return s; });
        demonstrate("F04_Seat_002", "R1 - Tạo đơn một ghế", () -> {
            BookingPage page = new BookingPage(driver, baseUrl);
            page.openShow(show);
            Seat seat = page.selectFirstAvailable(show);
            int id = page.submit();
            new PaymentPage(driver, baseUrl).assertOrderDetails(id, show, seat);
            db.bookingState(id, "PENDING", "UNPAID");
            assertEquals(1, db.number("SELECT COUNT(*) FROM bookings WHERE id=? AND user_id=(SELECT id FROM users WHERE email=?) AND quantity=1 AND total_amount=80000", id, config.account("customer").email()));
            assertEquals(1, db.number("SELECT COUNT(*) FROM booking_seats WHERE booking_id=? AND seat_id=? AND showtime_id=?", id, seat.id(), show.id()));
        });
    }

    @Test @Order(2)
    @DisplayName("F04_Seat_006 - R2: Suất CLOSED")
    void khongDatSuatDaDong() {
        Show show = prepare("Tạo suất CLOSED", () -> {
            Show s=createShow(); new AdminScheduleStatePage(driver,baseUrl).setStatus(s.id(),"CLOSED"); loginAs("customer"); return s;
        });
        demonstrate("F04_Seat_006", "R2 - Chặn URL đặt vé của suất CLOSED", () -> {
            driver.get(baseUrl + "/booking?showtimeId=" + show.id());
            assertEquals(baseUrl + "/showtimes", driver.getCurrentUrl());
            assertTrue(driver.findElements(By.id("bookingForm")).isEmpty());
            assertEquals(0, db.number("SELECT COUNT(*) FROM bookings WHERE showtime_id=?", show.id()));
        });
    }

    @Test @Order(3)
    @DisplayName("F04_Seat_001 - R3: Chưa chọn ghế")
    void khongDatKhiChuaChonGhe() {
        Show show = prepare("Tạo suất còn ghế", () -> { Show s=createShow(); loginAs("customer"); return s; });
        demonstrate("F04_Seat_001", "R3 - Chưa chọn ghế thì không tạo đơn", () -> {
            new BookingPage(driver,baseUrl).openShow(show);
            assertFalse(driver.findElement(By.id("submitBookingBtn")).isEnabled());
            assertEquals("0",driver.findElement(By.id("seatCount")).getText());
            assertEquals(0, db.number("SELECT COUNT(*) FROM bookings WHERE showtime_id=?",show.id()));
        });
    }

    @Test @Order(4)
    @DisplayName("F04_Seat_008 - R4: Ghế được người khác giữ")
    void khongChonGheDangGiu() {
        OrderData order=prepare("Khách A giữ một ghế",this::createUnpaidOrder);
        demonstrate("F04_Seat_008", "R4 - Khách B không chọn được ghế đang giữ", () -> {
            kiemTraGheKhongChonDuoc(order);
            db.bookingState(order.id(),"PENDING","UNPAID");
            assertSeatHeld(order);
            assertEquals(1,db.number("SELECT COUNT(*) FROM bookings WHERE showtime_id=?",order.show().id()));
        });
    }

    @Test @Order(5)
    @DisplayName("F04_Seat_007 - R5: Ghế đã thanh toán")
    void khongChonGheDaDat() {
        OrderData order=prepare("Chuẩn bị đơn đã thanh toán",()-> {OrderData o=createCounterOrder();confirmOrder(o);return o;});
        demonstrate("F04_Seat_007", "R5 - Ghế đã đặt không làm đổi số lượng/tổng", () -> {
            kiemTraGheKhongChonDuoc(order);
            db.bookingState(order.id(),"CONFIRMED","PAID");
            assertSeatHeld(order);
        });
    }

    @Test @Order(6)
    @DisplayName("F04_Seat_018 - R6: Ghế thuộc phòng khác")
    void tuChoiGheSaiPhong() {
        Show show=prepare("Tạo suất phòng 1",()-> {Show s=createShow();loginAs("customer");return s;});
        demonstrate("F04_Seat_018", "R6 - Gửi ID ghế phòng khác", () -> {
            int foreign=db.number("SELECT MIN(id) FROM seats WHERE room_id<>?",config.get("flow.roomId","1"));
            assertNotEquals(Integer.parseInt(config.get("flow.roomId","1")),db.number("SELECT room_id FROM seats WHERE id=?",foreign));
            new BookingPage(driver,baseUrl).openShow(show);
            browserPost("/booking","showtimeId",""+show.id(),"seatIds",""+foreign);
            assertTrue(driver.findElement(By.cssSelector(".error-box")).getText().contains("Danh sách ghế không hợp lệ"));
            assertEquals(0,db.number("SELECT COUNT(*) FROM bookings WHERE showtime_id=?",show.id()));
            assertEquals(0,db.number("SELECT COUNT(*) FROM booking_seats WHERE showtime_id=?",show.id()));
        });
    }

    private void kiemTraGheKhongChonDuoc(OrderData order) {
        assertNotEquals(config.account("customer").email(),config.account("otherCustomer").email());
        loginAs("otherCustomer");
        driver.get(baseUrl+"/booking?showtimeId="+order.show().id());
        WebElement input=driver.findElement(By.cssSelector("input[name=seatIds][value='"+order.seat().id()+"']"));
        assertFalse(input.isEnabled());
        input.findElement(By.xpath("following-sibling::span")).click();
        assertFalse(input.isSelected());
        assertEquals("0",driver.findElement(By.id("seatCount")).getText());
        assertEquals("0",driver.findElement(By.id("totalPrice")).getText().replaceAll("[^0-9]",""));
    }
}
