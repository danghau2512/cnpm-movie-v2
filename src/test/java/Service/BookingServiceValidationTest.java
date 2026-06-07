package Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
// issue 15
class BookingServiceValidationTest {

    @Test
    @DisplayName("Issue #15 - Không cho tạo booking khi danh sách ghế null")
    void createBooking_WithNullSeatIds_ShouldThrowException() {
        BookingService bookingService = new BookingService();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookingService.createBooking(1, 1, null)
        );

        assertEquals("Vui lòng chọn ít nhất một ghế.", exception.getMessage());
    }

    @Test
    @DisplayName("Issue #15 - Không cho tạo booking khi danh sách ghế rỗng")
    void createBooking_WithEmptySeatIds_ShouldThrowException() {
        BookingService bookingService = new BookingService();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                bookingService.createBooking(1, 1, Collections.emptyList())
        );

        assertEquals("Vui lòng chọn ít nhất một ghế.", exception.getMessage());
    }
}