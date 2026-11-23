package com.cinebook.tests.f08;

import com.cinebook.base.TechniqueFeatureTest;
import com.cinebook.pages.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F08BookingStateTest extends TechniqueFeatureTest {
    @Test @Order(1)
    @DisplayName("F08_Booking_010 - Xác nhận thu tiền, giữ ghế")
    void xacNhanDonTaiQuay() {
        OrderData order=prepare("Đơn tại quầy còn hạn",this::createCounterOrder);
        demonstrate("F08_Booking_010","PENDING/UNPAID → CONFIRMED/PAID",()-> {
            confirmOrder(order);
            db.bookingState(order.id(),"CONFIRMED","PAID");
            assertPayment(order.id(),"PAY_AT_COUNTER","SUCCESS");
            assertSeatHeld(order);
        });
    }

    @Test @Order(2)
    @DisplayName("F08_Booking_012 - Hủy đơn và giải phóng ghế")
    void huyDonChoThanhToan() {
        OrderData order=prepare("Đơn đang giữ ghế",this::createCounterOrder);
        demonstrate("F08_Booking_012","PENDING/UNPAID → CANCELLED/FAILED",()-> {
            loginAs("admin");
            new AdminBookingsPage(driver,baseUrl).cancel(order.code());
            db.bookingState(order.id(),"CANCELLED","FAILED");
            assertPayment(order.id(),"PAY_AT_COUNTER","FAILED");
            assertEquals(0,db.number("SELECT COUNT(*) FROM booking_seats WHERE booking_id=?",order.id()));
            evidence.step("DA-HUY", "Đơn cũ đã hủy, ghế được giải phóng", () -> assertTrue(body().contains("Đã hủy")));
            assertNotEquals(config.account("customer").email(),config.account("otherCustomer").email());
            loginAs("otherCustomer");
            BookingPage page=new BookingPage(driver,baseUrl);
            page.openReleasedAndSelect(order.show(),order.seat());
            int replacement=page.submit();
            assertNotEquals(order.id(),replacement);
            assertEquals(1,db.number("SELECT COUNT(*) FROM booking_seats WHERE booking_id=? AND seat_id=? AND showtime_id=?",replacement,order.seat().id(),order.show().id()));
            db.bookingState(order.id(),"CANCELLED","FAILED");
        });
    }

    @Test @Order(3)
    @DisplayName("F08_Booking_017 - Từ chối hủy đơn đã trả từ tab cũ")
    void khongHuyDonDaThanhToanTuTabCu() {
        OrderData order=prepare("Mở đơn chờ ở hai tab Admin",this::createCounterOrder);
        loginAs("admin");
        AdminBookingsPage page=new AdminBookingsPage(driver,baseUrl);
        page.find(order.code());
        String oldTab=driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        demonstrate("F08_Booking_017","Xác nhận ở tab mới rồi hủy từ tab cũ",()-> {
            page.confirm(order.code(),order.seat());
            String paid=db.paymentSnapshot(order.id());
            driver.close();
            driver.switchTo().window(oldTab);
            WebElement cancel=driver.findElement(By.cssSelector("button[value=cancel]"));
            cancel.click();
            wait.until(ExpectedConditions.alertIsPresent()).accept();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".notice.error")));
            assertTrue(driver.findElement(By.cssSelector(".notice.error")).getText().contains("Chỉ xử lý đơn đang chờ thanh toán."));
            db.bookingState(order.id(),"CONFIRMED","PAID");
            assertEquals(paid,db.paymentSnapshot(order.id()));
            assertSeatHeld(order);
            WebElement paidRow=driver.findElement(By.xpath("//tr[td/strong[text()='"+order.code()+"']]"));
            assertTrue(paidRow.getText().contains(order.seat().code()));
            assertTrue(paidRow.getText().contains("Đã xác nhận") && paidRow.getText().contains("Đã thanh toán"));
            assertTrue(paidRow.findElements(By.tagName("button")).isEmpty());
        });
    }
}
