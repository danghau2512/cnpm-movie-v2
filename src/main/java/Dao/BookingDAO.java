package Dao;

import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class BookingDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

    public int createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        return jdbi.inTransaction(handle -> {
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

                throw new RuntimeException("Suất chiếu không hợp lệ hoặc đã đóng.");
            }
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
                throw new RuntimeException("Ghế không thuộc phòng chiếu của suất chiếu này.");
            }

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
                throw new RuntimeException("Một hoặc nhiều ghế đã được đặt. Vui lòng chọn ghế khác.");
            }
            String bookingCode = "BK" + System.currentTimeMillis();
            int quantity = seatIds.size();
            long totalAmount = price * quantity;
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