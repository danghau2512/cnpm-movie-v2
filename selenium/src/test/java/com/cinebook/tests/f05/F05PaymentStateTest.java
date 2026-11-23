package com.cinebook.tests.f05;

import com.cinebook.base.TechniqueFeatureTest;
import com.cinebook.pages.PaymentPage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F05PaymentStateTest extends TechniqueFeatureTest {
    @Test @Order(1)
    @DisplayName("F05_Payment_002 - Chọn tại quầy, đơn vẫn chưa trả tiền")
    void chonThanhToanTaiQuay() {
        OrderData order=prepare("Chuẩn bị đơn chưa thanh toán",this::createUnpaidOrder);
        demonstrate("F05_Payment_002","PENDING/UNPAID sau khi chọn tại quầy",()-> {
            new PaymentPage(driver,baseUrl).payAtCounter(order.id(),order.show(),order.seat());
            db.bookingState(order.id(),"PENDING","UNPAID");
            assertPayment(order.id(),"PAY_AT_COUNTER","PENDING");
            assertSeatHeld(order);
        });
    }

    @Test @Order(2)
    @DisplayName("F05_Payment_003 - Admin thu tiền tại quầy")
    void xacNhanDaThuTien() {
        OrderData order=prepare("Đơn tại quầy còn hạn",this::createCounterOrder);
        demonstrate("F05_Payment_003","Chuyển CONFIRMED/PAID khi Admin xác nhận",()-> {
            confirmOrder(order);
            db.bookingState(order.id(),"CONFIRMED","PAID");
            assertPayment(order.id(),"PAY_AT_COUNTER","SUCCESS");
            assertSeatHeld(order);
        });
    }

    @Test @Order(3)
    @DisplayName("F05_Payment_008 - Không thanh toán lại đơn đã trả")
    void khongThanhToanHaiLan() {
        OrderData order=prepare("Đơn đã được thu tiền",()-> {OrderData o=createCounterOrder();confirmOrder(o);loginAs("customer");return o;});
        demonstrate("F05_Payment_008","Giữ giao dịch SUCCESS cũ khi gửi lại thanh toán",()-> {
            String before=db.paymentSnapshot(order.id());
            driver.get(baseUrl+"/payment?bookingId="+order.id());
            assertEquals(baseUrl+"/payment-result?bookingId="+order.id(),driver.getCurrentUrl());
            browserPost("/payment","bookingId",""+order.id(),"paymentMethod","PAY_AT_COUNTER");
            db.bookingState(order.id(),"CONFIRMED","PAID");
            assertEquals(before,db.paymentSnapshot(order.id()));
            assertPayment(order.id(),"PAY_AT_COUNTER","SUCCESS");
            assertSeatHeld(order);
        });
    }
}
