package Dao;

import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class BookingDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

    private static final int SEAT_HOLD_TIMEOUT_MINUTES = 10;

    /**
     * UC06 - Đặt vé
     * Tạo đơn đặt vé và lưu danh sách ghế khách hàng đã chọn.
     *
     * Ngày 10:
     * - Booking PENDING/UNPAID sẽ giữ ghế tạm thời trong SEAT_HOLD_TIMEOUT_MINUTES phút.
     * - Booking hết hạn giữ ghế sẽ bị hủy và xóa booking_seats để mở ghế cho người khác đặt.
     */
    public int createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        try {
            // UC06 - 6.1.17: BookingService gọi BookingDAO.createBooking(userId, showtimeId, seatIds)
            return jdbi.inTransaction(handle -> {

                // UC06 - 6.1.18: BookingDAO kiểm tra suất chiếu có tồn tại và đang OPEN hay không
                Long price = handle.createQuery("""
                                SELECT price
                                FROM showtimes
                                WHERE id = :showtimeId
                                AND status = 'OPEN'
                                """)
                        .bind("showtimeId", showtimeId)
                        .mapTo(Long.class)
                        .findOne()
                        .orElse(null);

                if (price == null) {
                    // UC06 - 6.1.6A.1: Suất chiếu không tồn tại hoặc không còn hợp lệ
                    throw new RuntimeException("Suất chiếu không hợp lệ hoặc đã đóng.");
                }

                // UC06 - 6.1.19: Giải phóng các booking PENDING/UNPAID đã hết thời gian giữ ghế
                releaseExpiredSeatHolds(handle, showtimeId);

                // UC06 - 6.1.20: Kiểm tra các ghế được chọn có thuộc đúng phòng chiếu của suất chiếu hay không
                Integer validSeatCount = handle.createQuery("""
                                SELECT COUNT(*)
                                FROM seats se
                                JOIN showtimes st ON se.room_id = st.room_id
                                WHERE st.id = :showtimeId
                                AND se.id IN (<seatIds>)
                                """)
                        .bind("showtimeId", showtimeId)
                        .bindList("seatIds", seatIds)
                        .mapTo(Integer.class)
                        .one();

                if (validSeatCount == null || validSeatCount != seatIds.size()) {
                    // UC06 - 6.1.20A.1: Dữ liệu ghế không hợp lệ hoặc ghế không thuộc phòng chiếu
                    throw new RuntimeException("Danh sách ghế không hợp lệ cho suất chiếu này.");
                }

                // UC06 - 6.1.20: Kiểm tra các ghế đã chọn có đang bị giữ hoặc đã được đặt hay không
                Integer bookedCount = handle.createQuery("""
                                SELECT COUNT(*)
                                FROM booking_seats bs
                                JOIN bookings b ON bs.booking_id = b.id
                                WHERE bs.showtime_id = :showtimeId
                                AND bs.seat_id IN (<seatIds>)
                                AND (
                                    b.booking_status = 'CONFIRMED'
                                    OR (
                                        b.booking_status = 'PENDING'
                                        AND b.payment_status = 'UNPAID'
                                        AND b.hold_expires_at > NOW()
                                    )
                                )
                                """)
                        .bind("showtimeId", showtimeId)
                        .bindList("seatIds", seatIds)
                        .mapTo(Integer.class)
                        .one();

                if (bookedCount != null && bookedCount > 0) {
                    // UC06 - 6.1.20A.1: Ghế đã được đặt hoặc đang được giữ bởi người khác
                    throw new RuntimeException("Một hoặc nhiều ghế đang được giữ hoặc đã được đặt. Vui lòng chọn ghế khác.");
                }

                String bookingCode = "BK" + System.currentTimeMillis();
                int quantity = seatIds.size();
                long totalAmount = price * quantity;

                // UC06 - 6.1.21: BookingDAO insertBooking(...) kèm thời gian giữ ghế tạm thời
                int bookingId = handle.createUpdate("""
                                INSERT INTO bookings
                                (user_id, showtime_id, booking_code, quantity, total_amount,
                                 booking_status, payment_status, hold_expires_at)
                                VALUES
                                (:userId, :showtimeId, :bookingCode, :quantity, :totalAmount,
                                 'PENDING', 'UNPAID', DATE_ADD(NOW(), INTERVAL 10 MINUTE))
                                """)
                        .bind("userId", userId)
                        .bind("showtimeId", showtimeId)
                        .bind("bookingCode", bookingCode)
                        .bind("quantity", quantity)
                        .bind("totalAmount", totalAmount)
                        .executeAndReturnGeneratedKeys("id")
                        .mapTo(Integer.class)
                        .one();

                // UC06 - 6.1.22: BookingDAO insertBookingSeats(...) để lưu danh sách ghế đã chọn
                for (Integer seatId : seatIds) {
                    handle.createUpdate("""
                                    INSERT INTO booking_seats
                                    (booking_id, showtime_id, seat_id, seat_price)
                                    VALUES
                                    (:bookingId, :showtimeId, :seatId, :seatPrice)
                                    """)
                            .bind("bookingId", bookingId)
                            .bind("showtimeId", showtimeId)
                            .bind("seatId", seatId)
                            .bind("seatPrice", price)
                            .execute();
                }

                // UC06 - 6.1.23: BookingDAO trả về bookingId cho BookingService sau khi transaction thành công
                return bookingId;
            });

        } catch (RuntimeException e) {
            /*
             * UC06 - 6.1.21A / 6.1.22A:
             * Nếu insertBooking(...) hoặc insertBookingSeats(...) thất bại,
             * jdbi.inTransaction sẽ rollback toàn bộ thao tác.
             */

            if (containsIgnoreCase(e, "duplicate")) {
                throw new RuntimeException("Một hoặc nhiều ghế vừa được người khác đặt. Vui lòng chọn ghế khác.");
            }

            throw e;
        }
    }

    /**
     * UC06 - 6.1.19:
     * Tìm các booking PENDING/UNPAID đã hết hạn giữ ghế và giải phóng ghế.
     *
     * Vì bảng booking_seats có unique(showtime_id, seat_id),
     * cần xóa booking_seats của booking hết hạn để ghế có thể được đặt lại.
     */
    private void releaseExpiredSeatHolds(org.jdbi.v3.core.Handle handle, int showtimeId) {
        List<Integer> expiredBookingIds = handle.createQuery("""
                        SELECT id
                        FROM bookings
                        WHERE showtime_id = :showtimeId
                        AND booking_status = 'PENDING'
                        AND payment_status = 'UNPAID'
                        AND hold_expires_at IS NOT NULL
                        AND hold_expires_at <= NOW()
                        """)
                .bind("showtimeId", showtimeId)
                .mapTo(Integer.class)
                .list();

        if (expiredBookingIds.isEmpty()) {
            return;
        }

        handle.createUpdate("""
                        DELETE FROM booking_seats
                        WHERE booking_id IN (<bookingIds>)
                        """)
                .bindList("bookingIds", expiredBookingIds)
                .execute();

        handle.createUpdate("""
                        UPDATE bookings
                        SET booking_status = 'CANCELLED',
                            payment_status = 'FAILED'
                        WHERE id IN (<bookingIds>)
                        """)
                .bindList("bookingIds", expiredBookingIds)
                .execute();
    }

    public void confirmBooking(int bookingId) {
        String sql = """
            UPDATE bookings
            SET booking_status = 'CONFIRMED',
                payment_status = 'PAID',
                hold_expires_at = NULL
            WHERE id = :bookingId
            AND booking_status = 'PENDING'
            AND payment_status = 'UNPAID'
            AND hold_expires_at > NOW()
            """;

        int updatedRows = jdbi.withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("bookingId", bookingId)
                        .execute()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("Đơn đặt vé đã hết thời gian giữ ghế hoặc không còn hợp lệ.");
        }
    }

    public void cancelBooking(int bookingId) {
        jdbi.useTransaction(handle -> {
            handle.createUpdate("""
                            DELETE FROM booking_seats
                            WHERE booking_id = :bookingId
                            """)
                    .bind("bookingId", bookingId)
                    .execute();

            handle.createUpdate("""
                            UPDATE bookings
                            SET booking_status = 'CANCELLED',
                                payment_status = 'FAILED',
                                hold_expires_at = NULL
                            WHERE id = :bookingId
                            """)
                    .bind("bookingId", bookingId)
                    .execute();
        });
    }

    /**
     * Kiểm tra message lỗi ở cả exception hiện tại và các nguyên nhân bên trong.
     * Dùng để bắt lỗi trùng ghế do unique key trong booking_seats.
     */
    private boolean containsIgnoreCase(Throwable throwable, String keyword) {
        while (throwable != null) {
            String message = throwable.getMessage();

            if (message != null && message.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }

            throwable = throwable.getCause();
        }

        return false;
    }
}