package Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentInfoTest {

    @Test
    @DisplayName("UC07 - Định dạng tổng tiền theo kiểu Việt Nam")
    void getTotalText_shouldFormatMoneyInVietnameseStyle() {
        PaymentInfo info = new PaymentInfo();
        info.setTotalAmount(new BigDecimal("180000"));

        assertEquals("180.000 VNĐ", info.getTotalText());
    }

    @Test
    @DisplayName("UC07 - Tổng tiền null phải hiển thị 0 VNĐ")
    void getTotalText_WithNullAmount_ShouldReturnZeroVnd() {
        PaymentInfo info = new PaymentInfo();

        assertEquals("0 VNĐ", info.getTotalText());
    }

    @Test
    @DisplayName("UC07 - Hiển thị trạng thái booking bằng tiếng Việt")
    void getBookingStatusText_shouldReturnVietnameseText() {
        PaymentInfo info = new PaymentInfo();

        info.setBookingStatus("PENDING");
        assertEquals("Chờ thanh toán", info.getBookingStatusText());

        info.setBookingStatus("CONFIRMED");
        assertEquals("Đã xác nhận", info.getBookingStatusText());

        info.setBookingStatus("CANCELLED");
        assertEquals("Đã hủy", info.getBookingStatusText());
    }

    @Test
    @DisplayName("UC07 - Hiển thị trạng thái payment bằng tiếng Việt")
    void getPaymentStatusText_shouldReturnVietnameseText() {
        PaymentInfo info = new PaymentInfo();

        info.setPaymentStatus("UNPAID");
        assertEquals("Chưa thanh toán", info.getPaymentStatusText());

        info.setPaymentStatus("PAID");
        assertEquals("Đã thanh toán", info.getPaymentStatusText());

        info.setPaymentStatus("FAILED");
        assertEquals("Thanh toán thất bại", info.getPaymentStatusText());
    }

    @Test
    @DisplayName("UC07 - Trả về class CSS đúng cho trạng thái booking")
    void getBookingStatusClass_shouldReturnCorrectCssClass() {
        PaymentInfo info = new PaymentInfo();

        info.setBookingStatus("PENDING");
        assertEquals("status-pending", info.getBookingStatusClass());

        info.setBookingStatus("CONFIRMED");
        assertEquals("status-success", info.getBookingStatusClass());

        info.setBookingStatus("CANCELLED");
        assertEquals("status-cancelled", info.getBookingStatusClass());
    }

    @Test
    @DisplayName("UC07 - Trả về class CSS đúng cho trạng thái payment")
    void getPaymentStatusClass_shouldReturnCorrectCssClass() {
        PaymentInfo info = new PaymentInfo();

        info.setPaymentStatus("UNPAID");
        assertEquals("status-pending", info.getPaymentStatusClass());

        info.setPaymentStatus("PAID");
        assertEquals("status-success", info.getPaymentStatusClass());

        info.setPaymentStatus("FAILED");
        assertEquals("status-cancelled", info.getPaymentStatusClass());
    }

    @Test
    @DisplayName("UC07 - Trạng thái booking không xác định dùng class mặc định")
    void getBookingStatusClass_WithUnknownStatus_ShouldReturnDefaultClass() {
        PaymentInfo info = new PaymentInfo();
        info.setBookingStatus("UNKNOWN");

        assertEquals("status-default", info.getBookingStatusClass());
    }

    @Test
    @DisplayName("UC07 - Trạng thái payment không xác định dùng class mặc định")
    void getPaymentStatusClass_WithUnknownStatus_ShouldReturnDefaultClass() {
        PaymentInfo info = new PaymentInfo();
        info.setPaymentStatus("UNKNOWN");

        assertEquals("status-default", info.getPaymentStatusClass());
    }
}