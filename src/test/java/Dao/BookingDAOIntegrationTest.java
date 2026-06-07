package Dao;

import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
// issue 14,15,18,36
class BookingDAOIntegrationTest {

    private static Jdbi jdbi;
    private BookingDAO bookingDAO;

    @BeforeAll
    static void beforeAll() {
        jdbi = JdbiConnector.getJdbi();
    }

    @BeforeEach
    void setUp() {
        bookingDAO = new BookingDAO();
    }

    @Test
    @DisplayName("Issue #15, #18, #36 - Tạo booking thành công phải lưu bookings, booking_seats và hold_expires_at")
    void createBooking_WithValidData_ShouldCreateBookingAndBookingSeats() {
        TestData data = findAvailableTestData(2);

        assumeTrue(data != null, "Bỏ qua test vì database không có đủ dữ liệu suất chiếu/ghế trống.");

        int bookingId = bookingDAO.createBooking(data.userId, data.showtimeId, data.seatIds);

        try {
            Integer bookingCount = jdbi.withHandle(handle ->
                    handle.createQuery("""
                                    SELECT COUNT(*)
                                    FROM bookings
                                    WHERE id = :bookingId
                                    AND booking_status = 'PENDING'
                                    AND payment_status = 'UNPAID'
                                    AND hold_expires_at IS NOT NULL
                                    """)
                            .bind("bookingId", bookingId)
                            .mapTo(Integer.class)
                            .one()
            );

            Integer bookingSeatCount = jdbi.withHandle(handle ->
                    handle.createQuery("""
                                    SELECT COUNT(*)
                                    FROM booking_seats
                                    WHERE booking_id = :bookingId
                                    """)
                            .bind("bookingId", bookingId)
                            .mapTo(Integer.class)
                            .one()
            );

            assertEquals(1, bookingCount);
            assertEquals(data.seatIds.size(), bookingSeatCount);

        } finally {
            bookingDAO.cancelBooking(bookingId);
        }
    }

    @Test
    @DisplayName("Issue #36 - Không cho đặt trùng ghế đang được giữ tạm thời")
    void createBooking_WithHeldSeat_ShouldThrowException() {
        TestData data = findAvailableTestData(1);

        assumeTrue(data != null, "Bỏ qua test vì database không có đủ dữ liệu suất chiếu/ghế trống.");

        int firstBookingId = bookingDAO.createBooking(data.userId, data.showtimeId, data.seatIds);

        try {
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    bookingDAO.createBooking(data.userId, data.showtimeId, data.seatIds)
            );

            assertTrue(
                    exception.getMessage().contains("ghế") || exception.getMessage().contains("Ghế"),
                    "Thông báo lỗi nên liên quan đến ghế đã được giữ hoặc đã được đặt."
            );

        } finally {
            bookingDAO.cancelBooking(firstBookingId);
        }
    }

    @Test
    @DisplayName("Issue #36 - Booking hết hạn giữ ghế phải được giải phóng khi tạo booking mới")
    void createBooking_WithExpiredHold_ShouldReleaseSeatAndAllowBookingAgain() {
        TestData data = findAvailableTestData(1);

        assumeTrue(data != null, "Bỏ qua test vì database không có đủ dữ liệu suất chiếu/ghế trống.");

        int expiredBookingId = bookingDAO.createBooking(data.userId, data.showtimeId, data.seatIds);

        jdbi.useHandle(handle ->
                handle.createUpdate("""
                                UPDATE bookings
                                SET hold_expires_at = DATE_SUB(NOW(), INTERVAL 1 MINUTE)
                                WHERE id = :bookingId
                                """)
                        .bind("bookingId", expiredBookingId)
                        .execute()
        );

        int newBookingId = bookingDAO.createBooking(data.userId, data.showtimeId, data.seatIds);

        try {
            Integer oldBookingCancelled = jdbi.withHandle(handle ->
                    handle.createQuery("""
                                    SELECT COUNT(*)
                                    FROM bookings
                                    WHERE id = :bookingId
                                    AND booking_status = 'CANCELLED'
                                    """)
                            .bind("bookingId", expiredBookingId)
                            .mapTo(Integer.class)
                            .one()
            );

            Integer newBookingExists = jdbi.withHandle(handle ->
                    handle.createQuery("""
                                    SELECT COUNT(*)
                                    FROM bookings
                                    WHERE id = :bookingId
                                    AND booking_status = 'PENDING'
                                    AND payment_status = 'UNPAID'
                                    """)
                            .bind("bookingId", newBookingId)
                            .mapTo(Integer.class)
                            .one()
            );

            assertEquals(1, oldBookingCancelled);
            assertEquals(1, newBookingExists);

        } finally {
            bookingDAO.cancelBooking(newBookingId);
        }
    }

    @Test
    @DisplayName("Issue #36 - Không xác nhận thanh toán nếu booking đã hết hạn giữ ghế")
    void confirmBooking_WithExpiredHold_ShouldThrowException() {
        TestData data = findAvailableTestData(1);

        assumeTrue(data != null, "Bỏ qua test vì database không có đủ dữ liệu suất chiếu/ghế trống.");

        int bookingId = bookingDAO.createBooking(data.userId, data.showtimeId, data.seatIds);

        try {
            jdbi.useHandle(handle ->
                    handle.createUpdate("""
                                    UPDATE bookings
                                    SET hold_expires_at = DATE_SUB(NOW(), INTERVAL 1 MINUTE)
                                    WHERE id = :bookingId
                                    """)
                            .bind("bookingId", bookingId)
                            .execute()
            );

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    bookingDAO.confirmBooking(bookingId)
            );

            assertTrue(exception.getMessage().contains("hết thời gian giữ ghế")
                    || exception.getMessage().contains("không còn hợp lệ"));

        } finally {
            bookingDAO.cancelBooking(bookingId);
        }
    }

    @Test
    @DisplayName("Issue #36 - Xác nhận booking còn hạn giữ ghế phải chuyển sang CONFIRMED và PAID")
    void confirmBooking_WithValidHold_ShouldUpdateStatus() {
        TestData data = findAvailableTestData(1);

        assumeTrue(data != null, "Bỏ qua test vì database không có đủ dữ liệu suất chiếu/ghế trống.");

        int bookingId = bookingDAO.createBooking(data.userId, data.showtimeId, data.seatIds);

        bookingDAO.confirmBooking(bookingId);

        try {
            Integer confirmedCount = jdbi.withHandle(handle ->
                    handle.createQuery("""
                                    SELECT COUNT(*)
                                    FROM bookings
                                    WHERE id = :bookingId
                                    AND booking_status = 'CONFIRMED'
                                    AND payment_status = 'PAID'
                                    AND hold_expires_at IS NULL
                                    """)
                            .bind("bookingId", bookingId)
                            .mapTo(Integer.class)
                            .one()
            );

            assertEquals(1, confirmedCount);

        } finally {
            bookingDAO.cancelBooking(bookingId);
        }
    }

    private TestData findAvailableTestData(int seatCount) {
        Optional<Integer> userIdOptional = jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id
                                FROM users
                                LIMIT 1
                                """)
                        .mapTo(Integer.class)
                        .findOne()
        );

        if (userIdOptional.isEmpty()) {
            return null;
        }

        Optional<Integer> showtimeIdOptional = jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT id
                                FROM showtimes
                                WHERE status = 'OPEN'
                                LIMIT 1
                                """)
                        .mapTo(Integer.class)
                        .findOne()
        );

        if (showtimeIdOptional.isEmpty()) {
            return null;
        }

        int showtimeId = showtimeIdOptional.get();

        List<Integer> seatIds = jdbi.withHandle(handle ->
                handle.createQuery("""
                        SELECT se.id
                        FROM seats se
                        JOIN showtimes st ON st.room_id = se.room_id
                        WHERE st.id = :showtimeId
                        AND NOT EXISTS (
                            SELECT 1
                            FROM booking_seats bs
                            WHERE bs.showtime_id = st.id
                            AND bs.seat_id = se.id
                        )
                        LIMIT :seatCount
                        """)
                        .bind("showtimeId", showtimeId)
                        .bind("seatCount", seatCount)
                        .mapTo(Integer.class)
                        .list()
        );

        if (seatIds.size() < seatCount) {
            return null;
        }

        return new TestData(userIdOptional.get(), showtimeId, seatIds);
    }

    private static class TestData {
        private final int userId;
        private final int showtimeId;
        private final List<Integer> seatIds;

        private TestData(int userId, int showtimeId, List<Integer> seatIds) {
            this.userId = userId;
            this.showtimeId = showtimeId;
            this.seatIds = seatIds;
        }
    }
}