package Dao;

import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class BookingDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();


    /**
     * UC06 - Đặt vé
     * UC06 - 6.1.10: Xử lý tạo đơn đặt vé trong database.
     * UC06 - 6.1.11: Kiểm tra suất chiếu hợp lệ.
     * UC06 - 6.1.12: Kiểm tra ghế còn trống.
     * UC06 - 6.1.13: Lưu đơn đặt vé vào bảng bookings.
     * UC06 - 6.1.14: Lưu ghế đã chọn vào bảng booking_seats.
     * UC06 - 6.1.15: Trả về bookingId sau khi tạo đơn thành công.
     */
    public int createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        // UC06 - 6.1.10: Bắt đầu transaction để đảm bảo tạo booking và lưu ghế cùng thành công hoặc cùng thất bại
        return jdbi.inTransaction(handle -> {
            // UC06 - 6.1.11: Kiểm tra suất chiếu có tồn tại và đang OPEN hay không
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
                // UC06 - 6.2.12: Suất chiếu không hợp lệ hoặc đã đóng
                throw new RuntimeException("Suất chiếu không hợp lệ hoặc đã đóng.");
            }
            // UC06 - 6.1.12: Kiểm tra các ghế được chọn có thuộc đúng phòng chiếu của suất chiếu không
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
                // UC06 - 6.2.13: Ghế không thuộc phòng chiếu của suất chiếu nên không cho tạo đơn
                throw new RuntimeException("Ghế không thuộc phòng chiếu của suất chiếu này.");
            }
            // UC06 - 6.1.12: Kiểm tra các ghế đã chọn có đang nằm trong đơn PENDING hoặc CONFIRMED hay không

            Integer bookedCount = handle.createQuery("""
                SELECT COUNT(*)
                FROM booking_seats bs
                JOIN bookings b ON bs.booking_id = b.id
                WHERE bs.showtime_id = :showtimeId
                AND bs.seat_id IN (<seatIds>)
                AND b.booking_status IN ('PENDING', 'CONFIRMED')
                """)
                    .bind("showtimeId", showtimeId)
                    .bindList("seatIds", seatIds)
                    .mapTo(Integer.class)
                    .one();

            if (bookedCount != null && bookedCount > 0) {
                // UC06 - 6.2.7: Phát hiện một hoặc nhiều ghế đã được người khác đặt
                throw new RuntimeException("Một hoặc nhiều ghế đã được đặt. Vui lòng chọn ghế khác.");
            }
            // UC06 - 6.1.13: Chuẩn bị dữ liệu để tạo đơn đặt vé
            String bookingCode = "BK" + System.currentTimeMillis();
            int quantity = seatIds.size();
            long totalAmount = price * quantity;
            // UC06 - 6.1.13: Lưu thông tin đơn đặt vé vào bảng bookings với trạng thái chờ thanh toán
            int bookingId = handle.createUpdate("""
                INSERT INTO bookings
                (user_id, showtime_id, booking_code, quantity, total_amount,
                 booking_status, payment_status)
                VALUES
                (:userId, :showtimeId, :bookingCode, :quantity, :totalAmount,
                 'PENDING', 'UNPAID')
                """)
                    .bind("userId", userId)
                    .bind("showtimeId", showtimeId)
                    .bind("bookingCode", bookingCode)
                    .bind("quantity", quantity)
                    .bind("totalAmount", totalAmount)
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo(Integer.class)
                    .one();
            // UC06 - 6.1.14: Lưu từng ghế khách hàng đã chọn vào bảng booking_seats
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
            // UC06 - 6.1.15: Trả về bookingId cho BookingService sau khi tạo đơn thành công
            return bookingId;
        });
    }
    public void confirmBooking(int bookingId) {
        String sql = """
            UPDATE bookings
            SET booking_status = 'CONFIRMED',
                payment_status = 'PAID'
            WHERE id = :bookingId
            """;

        jdbi.useHandle(handle ->
                handle.createUpdate(sql)
                        .bind("bookingId", bookingId)
                        .execute()
        );
    }
    public void cancelBooking(int bookingId) {
        String sql = """
            UPDATE bookings
            SET booking_status = 'CANCELLED',
                payment_status = 'FAILED'
            WHERE id = :bookingId
            """;

        jdbi.useHandle(handle ->
                handle.createUpdate(sql)
                        .bind("bookingId", bookingId)
                        .execute()
        );
    }
}