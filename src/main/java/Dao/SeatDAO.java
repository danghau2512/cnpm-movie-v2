package Dao;

import Model.Seat;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.List;

public class SeatDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

    /**
     * UC06 - Đặt vé
     * Lấy danh sách ghế theo suất chiếu và xác định trạng thái ghế đã được đặt hay chưa.
     */
    public List<Seat> findSeatsByShowtimeId(int showtimeId) {
        // UC06 - 6.1.8: SeatDAO truy vấn danh sách ghế và trạng thái ghế theo showtimeId
        String sql = """
            SELECT
                se.id,
                se.room_id AS roomId,
                se.seat_row AS seatRow,
                se.seat_number AS seatNumber,
                se.seat_code AS seatCode,
                CASE
                    WHEN EXISTS (
                        SELECT 1
                        FROM booking_seats bs
                        JOIN bookings b ON b.id = bs.booking_id
                        WHERE bs.showtime_id = st.id
                        AND bs.seat_id = se.id
                        AND b.booking_status IN ('PENDING', 'CONFIRMED')
                    )
                    THEN true
                    ELSE false
                END AS booked
            FROM showtimes st
            JOIN seats se ON st.room_id = se.room_id
            WHERE st.id = :showtimeId
            ORDER BY se.seat_row, se.seat_number
            """;

        List<Seat> seats = jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .registerRowMapper(BeanMapper.factory(Seat.class))
                        .mapTo(Seat.class)
                        .list()
        );

        // UC06 - 6.1.9: SeatDAO trả về danh sách ghế và trạng thái ghế cho BookingService
        return seats;
    }
}