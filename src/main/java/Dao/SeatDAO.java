package Dao;

import Model.Seat;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.List;

public class SeatDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

    public List<Seat> findSeatsByShowtimeId(int showtimeId) {
        String sql = """
            SELECT
                se.id,
                se.room_id AS roomId,
                se.seat_row AS seatRow,
                se.seat_number AS seatNumber,
                se.seat_code AS seatCode,
                CASE
                    WHEN b.id IS NULL THEN false
                    ELSE true
                END AS booked
            FROM showtimes st
            JOIN seats se ON st.room_id = se.room_id
            LEFT JOIN booking_seats bs
                ON bs.showtime_id = st.id
                AND bs.seat_id = se.id
            LEFT JOIN bookings b
                ON b.id = bs.booking_id
                AND b.booking_status IN ('PENDING', 'CONFIRMED')
            WHERE st.id = :showtimeId
            ORDER BY se.seat_row, se.seat_number
            """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .registerRowMapper(BeanMapper.factory(Seat.class))
                        .mapTo(Seat.class)
                        .list()
        );
    }}