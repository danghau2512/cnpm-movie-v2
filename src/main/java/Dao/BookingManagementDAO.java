package Dao;

import Model.BookingRecord;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import java.util.List;

public class BookingManagementDAO {
    private final Jdbi jdbi;
    public BookingManagementDAO() { this(JdbiConnector.getJdbi()); }
    public BookingManagementDAO(Jdbi jdbi) { this.jdbi = jdbi; }

    public List<BookingRecord> findBookings(Integer userId, String status, String keyword) {
        // LEFT JOIN giữ đơn đã hủy/hết hạn dù flow cũ đã giải phóng booking_seats.
        return jdbi.withHandle(h -> h.createQuery("""
                SELECT b.id AS bookingId,b.user_id AS userId,b.booking_code AS bookingCode,
                u.full_name AS customerName,u.email AS customerEmail,m.title AS movieTitle,r.name AS roomName,
                DATE_FORMAT(st.start_time,'%d/%m/%Y') AS showDate,DATE_FORMAT(st.start_time,'%H:%i') AS showTime,
                DATE_FORMAT(b.created_at,'%d/%m/%Y %H:%i') AS createdAt,b.quantity,b.total_amount AS totalAmount,
                CASE WHEN b.booking_status='PENDING' AND b.payment_status='UNPAID' AND b.hold_expires_at<=NOW()
                     THEN 'CANCELLED' ELSE b.booking_status END AS bookingStatus,
                CASE WHEN b.booking_status='PENDING' AND b.payment_status='UNPAID' AND b.hold_expires_at<=NOW()
                     THEN 'FAILED' ELSE b.payment_status END AS paymentStatus,
                (b.booking_status='PENDING' AND b.payment_status='UNPAID' AND b.hold_expires_at>NOW()) AS payable,
                (SELECT p.payment_method FROM payments p WHERE p.booking_id=b.id ORDER BY p.id DESC LIMIT 1) AS paymentMethod,
                (SELECT GROUP_CONCAT(se.seat_code ORDER BY se.seat_row,se.seat_number SEPARATOR ', ')
                    FROM booking_seats bs JOIN seats se ON se.id=bs.seat_id WHERE bs.booking_id=b.id) AS seats
                FROM bookings b JOIN users u ON u.id=b.user_id JOIN showtimes st ON st.id=b.showtime_id
                JOIN movies m ON m.id=st.movie_id JOIN rooms r ON r.id=st.room_id
                WHERE (:userId IS NULL OR b.user_id=:userId)
                AND (:keyword='' OR b.booking_code LIKE :pattern OR u.email LIKE :pattern OR m.title LIKE :pattern)
                AND (:status='' OR CASE WHEN b.booking_status='PENDING' AND b.payment_status='UNPAID'
                    AND b.hold_expires_at<=NOW() THEN 'CANCELLED' ELSE b.booking_status END=:status)
                ORDER BY b.created_at DESC,b.id DESC
                """).bind("userId", userId).bind("status", status).bind("keyword", keyword)
                .bind("pattern", "%" + keyword + "%").mapToBean(BookingRecord.class).list());
    }

    public void updateBooking(int id, String action) {
        jdbi.useTransaction(h -> {
            BookingRecord b = h.createQuery("""
                    SELECT id AS bookingId,booking_status AS bookingStatus,payment_status AS paymentStatus,
                    (hold_expires_at > NOW()) AS payable FROM bookings WHERE id=:id FOR UPDATE
                    """).bind("id", id).mapToBean(BookingRecord.class).findOne()
                    .orElseThrow(() -> new IllegalArgumentException("Đơn đặt vé không tồn tại."));
            if (!"PENDING".equals(b.getBookingStatus()) || !"UNPAID".equals(b.getPaymentStatus()))
                throw new IllegalArgumentException("Chỉ xử lý đơn đang chờ thanh toán. Đơn đã thanh toán cần quy trình hoàn tiền riêng.");
            if ("confirm".equals(action)) {
                if (!b.isPayable()) throw new IllegalArgumentException("Đơn đã hết thời gian giữ ghế.");
                int updated = h.createUpdate("""
                        UPDATE payments SET payment_status='SUCCESS',paid_at=NOW()
                        WHERE booking_id=:id AND payment_method='PAY_AT_COUNTER' AND payment_status='PENDING'
                        """).bind("id", id).execute();
                if (updated == 0) throw new IllegalArgumentException("Chỉ xác nhận đơn đã chọn thanh toán tại quầy.");
                h.createUpdate("UPDATE bookings SET booking_status='CONFIRMED',payment_status='PAID',hold_expires_at=NULL WHERE id=:id")
                        .bind("id", id).execute();
            } else if ("cancel".equals(action)) {
                h.createUpdate("UPDATE payments SET payment_status='FAILED' WHERE booking_id=:id AND payment_status='PENDING'")
                        .bind("id", id).execute();
                h.createUpdate("DELETE FROM booking_seats WHERE booking_id=:id").bind("id", id).execute();
                h.createUpdate("UPDATE bookings SET booking_status='CANCELLED',payment_status='FAILED',hold_expires_at=NULL WHERE id=:id")
                        .bind("id", id).execute();
            } else throw new IllegalArgumentException("Thao tác không hợp lệ.");
        });
    }
}
