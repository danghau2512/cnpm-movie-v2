package com.cinebook.tests.f05;

import com.cinebook.base.FeatureMainTest;
import com.cinebook.pages.PaymentPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class F05PaymentMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F05 main | Choose payment at counter")
    void chooseCounterPayment() {
        OrderData order = prepare("Create a Customer order, stop at payment", this::createUnpaidOrder);
        evidence.step("F05-counter", "Counter payment remains PENDING/UNPAID; reference F05_Payment_002", () -> {
            PaymentPage payment = new PaymentPage(driver, baseUrl);
            payment.assertOrderDetails(order.id(), order.show(), order.seat());
            String code = payment.payAtCounter(order.id(), order.show(), order.seat());
            payment.assertHistory(order.id(), code, "Chờ thanh toán", "Chưa thanh toán");
        });
    }
}
