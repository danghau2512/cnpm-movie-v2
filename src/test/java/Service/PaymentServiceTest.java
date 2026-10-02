package Service;

import Model.PaymentInfo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {
    @Test
    void createVnpayUrlIncludesActualBookingExpiry() {
        Dao.PaymentDAO dao = org.mockito.Mockito.mock(Dao.PaymentDAO.class);
        jakarta.servlet.http.HttpServletRequest request = org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        PaymentInfo info = new PaymentInfo();
        info.setBookingStatus("PENDING"); info.setPaymentStatus("UNPAID");
        info.setBookingCode("BK_TEST"); info.setTotalAmount(new java.math.BigDecimal("90000"));
        info.setHoldExpiresAt(java.time.LocalDateTime.of(2026, 10, 3, 12, 10));
        org.mockito.Mockito.when(dao.findPaymentInfo(15)).thenReturn(info);
        org.mockito.Mockito.when(request.getScheme()).thenReturn("http");
        org.mockito.Mockito.when(request.getServerName()).thenReturn("localhost");
        org.mockito.Mockito.when(request.getServerPort()).thenReturn(8080);
        org.mockito.Mockito.when(request.getContextPath()).thenReturn("/cinebook");
        org.mockito.Mockito.when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        String url = new PaymentService(dao).createVnpayPaymentUrl(15, request);
        assertTrue(url.contains("vnp_ExpireDate=20261003121000"));
        assertTrue(url.contains("vnp_Amount=9000000"));
        org.mockito.Mockito.verify(dao).createVnpayPendingPayment(org.mockito.ArgumentMatchers.eq(15), org.mockito.ArgumentMatchers.startsWith("15_"));
    }

    @Test
    void canPay_WithPendingBookingAndUnpaidPayment_ShouldReturnTrue() {
        PaymentService service = new PaymentService();

        PaymentInfo info = new PaymentInfo();
        info.setBookingStatus("PENDING");
        info.setPaymentStatus("UNPAID");

        assertTrue(service.canPay(info));
    }

    @Test
    void canPay_WithConfirmedBookingAndPaidPayment_ShouldReturnFalse() {
        PaymentService service = new PaymentService();

        PaymentInfo info = new PaymentInfo();
        info.setBookingStatus("CONFIRMED");
        info.setPaymentStatus("PAID");

        assertFalse(service.canPay(info));
    }

    @Test
    void canPay_WithCancelledBookingAndFailedPayment_ShouldReturnFalse() {
        PaymentService service = new PaymentService();

        PaymentInfo info = new PaymentInfo();
        info.setBookingStatus("CANCELLED");
        info.setPaymentStatus("FAILED");

        assertFalse(service.canPay(info));
    }

    @Test
    void canPay_WithPendingBookingButPaidPayment_ShouldReturnFalse() {
        PaymentService service = new PaymentService();

        PaymentInfo info = new PaymentInfo();
        info.setBookingStatus("PENDING");
        info.setPaymentStatus("PAID");

        assertFalse(service.canPay(info));
    }

    @Test
    void canPay_WithConfirmedBookingButUnpaidPayment_ShouldReturnFalse() {
        PaymentService service = new PaymentService();

        PaymentInfo info = new PaymentInfo();
        info.setBookingStatus("CONFIRMED");
        info.setPaymentStatus("UNPAID");

        assertFalse(service.canPay(info));
    }

    @Test
    void canPay_WithNullPaymentInfo_ShouldReturnFalse() {
        PaymentService service = new PaymentService();

        assertFalse(service.canPay(null));
    }

    @Test
    void extractBookingIdFromTxnRef_WithValidTxnRef_ShouldReturnBookingId() throws Exception {
        int bookingId = callExtractBookingIdFromTxnRef("15_20260607103000");

        assertEquals(15, bookingId);
    }

    @Test
    void extractBookingIdFromTxnRef_WithAnotherValidTxnRef_ShouldReturnBookingId() throws Exception {
        int bookingId = callExtractBookingIdFromTxnRef("99_1717740000000");

        assertEquals(99, bookingId);
    }

    @Test
    void extractBookingIdFromTxnRef_WithNullTxnRef_ShouldThrowRuntimeException() {
        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                () -> callExtractBookingIdFromTxnRef(null)
        );

        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    @Test
    void extractBookingIdFromTxnRef_WithEmptyTxnRef_ShouldThrowRuntimeException() {
        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                () -> callExtractBookingIdFromTxnRef("")
        );

        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    @Test
    void extractBookingIdFromTxnRef_WithoutUnderscore_ShouldThrowRuntimeException() {
        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                () -> callExtractBookingIdFromTxnRef("1520260607103000")
        );

        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    @Test
    void extractBookingIdFromTxnRef_WithInvalidBookingId_ShouldThrowRuntimeException() {
        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                () -> callExtractBookingIdFromTxnRef("abc_20260607103000")
        );

        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    private int callExtractBookingIdFromTxnRef(String txnRef) throws Exception {
        PaymentService service = new PaymentService();

        Method method = PaymentService.class.getDeclaredMethod(
                "extractBookingIdFromTxnRef",
                String.class
        );

        method.setAccessible(true);

        return (Integer) method.invoke(service, txnRef);
    }
}
