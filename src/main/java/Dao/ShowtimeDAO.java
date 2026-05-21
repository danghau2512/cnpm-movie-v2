package Dao;

import Model.Showtime;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.List;

public class ShowtimeDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

    // UC05 - 4.7.6: Lấy tất cả các suất chiếu đang mở khi khách hàng không chọn phim cụ thể
    // UC05 - 4.8.2: Truy vấn danh sách lịch chiếu trong trường hợp không có movieId
    public List<Showtime> findAllOpen() {
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
                ORDER BY s.start_time ASC
                """;

// UC05 - 4.7.8: Truy vấn showtimes, movies, rooms và chỉ lấy suất chiếu có trạng thái OPEN(dòng 31)
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .registerRowMapper(BeanMapper.factory(Showtime.class))
                        .mapTo(Showtime.class)
                        .list()
        );
    }

    // UC05 - 4.7.7: Lấy danh sách suất chiếu của phim được chọn
    // UC05 - 4.8.6: Xử lý luồng thay thế khi request có movieId
    public List<Showtime> findByMovieId(int movieId) {
        // UC05 - 4.7.8: Truy vấn lịch chiếu theo phim và chỉ lấy suất chiếu đang mở
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
                AND s.movie_id = :movieId
                ORDER BY s.start_time ASC
                """;

        // UC05 - 4.7.9: Chuyển dữ liệu truy vấn thành danh sách đối tượng Showtime
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("movieId", movieId)
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