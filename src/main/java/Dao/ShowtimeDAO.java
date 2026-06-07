package Dao;

import Model.Showtime;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.List;

public class ShowtimeDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

    // UC05 - 4.7.6: Lấy tất cả các suất chiếu đang mở khi khách hàng không chọn phim cụ thể
    // UC05 - Cải tiến: Cho phép lọc lịch chiếu theo ngày và thể loại phim
    public List<Showtime> findAllOpen(String showDate, String genreName) {
        String sql = """
                SELECT
                    s.id,
                    s.movie_id AS movieId,
                    s.room_id AS roomId,
                    m.title AS movieTitle,
                    r.name AS roomName,
                    DATE_FORMAT(s.start_time, '%d/%m/%Y') AS showDate,
                    DATE_FORMAT(s.start_time, '%H:%i') AS showTime,
                    s.price,
                    FORMAT(s.price, 0) AS priceText,
                    s.status
                FROM showtimes s
                JOIN movies m ON s.movie_id = m.id
                JOIN rooms r ON s.room_id = r.id
                WHERE s.status = 'OPEN'
                AND s.start_time >= NOW()
                AND (:showDate IS NULL OR DATE(s.start_time) = :showDate)
                AND (
                    :genreName IS NULL
                    OR EXISTS (
                        SELECT 1
                        FROM movie_genres mg
                        JOIN genres g ON mg.genre_id = g.id
                        WHERE mg.movie_id = m.id
                        AND g.name = :genreName
                    )
                )
                ORDER BY s.start_time ASC
                """;

        // UC05 - 4.7.8: Truy vấn showtimes, movies, rooms và chỉ lấy suất chiếu hợp lệ
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showDate", showDate)
                        .bind("genreName", genreName)
                        .registerRowMapper(BeanMapper.factory(Showtime.class))
                        .mapTo(Showtime.class)
                        .list()
        );
    }

    // UC05 - 4.7.7: Lấy danh sách suất chiếu của phim được chọn
    // UC05 - Cải tiến: Có thể kết hợp lọc theo ngày và thể loại phim
    public List<Showtime> findByMovieId(int movieId, String showDate, String genreName) {
        String sql = """
                SELECT
                    s.id,
                    s.movie_id AS movieId,
                    s.room_id AS roomId,
                    m.title AS movieTitle,
                    r.name AS roomName,
                    DATE_FORMAT(s.start_time, '%d/%m/%Y') AS showDate,
                    DATE_FORMAT(s.start_time, '%H:%i') AS showTime,
                    s.price,
                    FORMAT(s.price, 0) AS priceText,
                    s.status
                FROM showtimes s
                JOIN movies m ON s.movie_id = m.id
                JOIN rooms r ON s.room_id = r.id
                WHERE s.status = 'OPEN'
                AND s.start_time >= NOW()
                AND s.movie_id = :movieId
                AND (:showDate IS NULL OR DATE(s.start_time) = :showDate)
                AND (
                    :genreName IS NULL
                    OR EXISTS (
                        SELECT 1
                        FROM movie_genres mg
                        JOIN genres g ON mg.genre_id = g.id
                        WHERE mg.movie_id = m.id
                        AND g.name = :genreName
                    )
                )
                ORDER BY s.start_time ASC
                """;

        // UC05 - 4.7.9: Chuyển dữ liệu truy vấn thành danh sách đối tượng Showtime
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
                        .bind("showDate", showDate)
                        .bind("genreName", genreName)
                        .registerRowMapper(BeanMapper.factory(Showtime.class))
                        .mapTo(Showtime.class)
                        .list()
        );
    }

    // UC06 - 6.1.3: Truy vấn thông tin suất chiếu theo showtimeId.
    public Showtime findById(int showtimeId) {
        String sql = """
            SELECT
                s.id,
                s.movie_id AS movieId,
                s.room_id AS roomId,
                m.title AS movieTitle,
                r.name AS roomName,
                DATE_FORMAT(s.start_time, '%d/%m/%Y') AS showDate,
                DATE_FORMAT(s.start_time, '%H:%i') AS showTime,
                s.price,
                FORMAT(s.price, 0) AS priceText,
                s.status
            FROM showtimes s
            JOIN movies m ON s.movie_id = m.id
            JOIN rooms r ON s.room_id = r.id
            WHERE s.id = :showtimeId
            """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("showtimeId", showtimeId)
                        .registerRowMapper(BeanMapper.factory(Showtime.class))
                        .mapTo(Showtime.class)
                        .findOne()
                        .orElse(null)
        );
    }
}
