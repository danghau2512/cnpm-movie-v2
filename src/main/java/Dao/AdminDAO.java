package Dao;

import Model.Movie;
import Model.Showtime;
import Util.JdbiConnector;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AdminDAO {
    private final Jdbi jdbi;
    public AdminDAO() { this(JdbiConnector.getJdbi()); }
    public AdminDAO(Jdbi jdbi) { this.jdbi = jdbi; }

    public List<Movie> movies() {
        return jdbi.withHandle(h -> h.createQuery("""
                SELECT id, title, duration_minutes AS durationMinutes, age_rating AS ageRating,
                short_description AS shortDescription, description, poster_url AS posterUrl,
                trailer_url AS trailerUrl, CAST(release_date AS CHAR) AS releaseDate, status
                FROM movies ORDER BY id DESC
                """).mapToBean(Movie.class).list());
    }

    public List<Map<String, Object>> genres() {
        return jdbi.withHandle(h -> h.createQuery("SELECT id, name FROM genres ORDER BY name").mapToMap().list());
    }
    public List<Integer> movieGenres(int id) {
        return jdbi.withHandle(h -> h.createQuery("SELECT genre_id FROM movie_genres WHERE movie_id=:id")
                .bind("id", id).mapTo(Integer.class).list());
    }
    public List<Map<String, Object>> rooms() {
        return jdbi.withHandle(h -> h.createQuery("SELECT id, name, status FROM rooms ORDER BY id").mapToMap().list());
    }
    public List<Showtime> showtimes() {
        return jdbi.withHandle(h -> h.createQuery("""
                SELECT s.id, s.movie_id AS movieId, s.room_id AS roomId, m.title AS movieTitle,
                r.name AS roomName, DATE_FORMAT(s.start_time, '%Y-%m-%d') AS showDate,
                DATE_FORMAT(s.start_time, '%H:%i') AS showTime, s.price, s.status
                FROM showtimes s JOIN movies m ON m.id=s.movie_id JOIN rooms r ON r.id=s.room_id
                ORDER BY s.start_time DESC
                """).mapToBean(Showtime.class).list());
    }

    public void saveMovie(Movie movie, List<Integer> genres) {
        jdbi.useTransaction(h -> {
            if (movie.getId() > 0) {
                Movie old = h.createQuery("SELECT id, duration_minutes AS durationMinutes FROM movies WHERE id=:id FOR UPDATE")
                        .bind("id", movie.getId()).mapToBean(Movie.class).findOne()
                        .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại."));
                if (old.getDurationMinutes() != movie.getDurationMinutes() && count(h,
                        "SELECT COUNT(*) FROM showtimes WHERE movie_id=:id", movie.getId()) > 0)
                    throw new IllegalArgumentException("Không đổi thời lượng phim đã có lịch chiếu. Hãy xử lý lịch chiếu trước.");
                if (!"NOW_SHOWING".equals(movie.getStatus()) && count(h,
                        "SELECT COUNT(*) FROM showtimes WHERE movie_id=:id AND status='OPEN' AND start_time > NOW()", movie.getId()) > 0)
                    throw new IllegalArgumentException("Hãy đóng lịch chiếu đang mở trước khi đổi phim sang sắp chiếu hoặc ẩn.");
            }
            if (!genres.isEmpty() && h.createQuery("SELECT COUNT(*) FROM genres WHERE id IN (<ids>)")
                    .bindList("ids", genres).mapTo(Integer.class).one() != genres.size())
                throw new IllegalArgumentException("Thể loại không hợp lệ.");
            String sql = movie.getId() == 0 ? """
                    INSERT INTO movies (title,duration_minutes,age_rating,short_description,description,poster_url,trailer_url,release_date,status)
                    VALUES (:title,:durationMinutes,:ageRating,:shortDescription,:description,:posterUrl,:trailerUrl,:releaseDate,:status)
                    """ : """
                    UPDATE movies SET title=:title,duration_minutes=:durationMinutes,age_rating=:ageRating,
                    short_description=:shortDescription,description=:description,poster_url=:posterUrl,
                    trailer_url=:trailerUrl,release_date=:releaseDate,status=:status WHERE id=:id
                    """;
            if (movie.getId() == 0) movie.setId(h.createUpdate(sql).bindBean(movie)
                    .executeAndReturnGeneratedKeys("id").mapTo(Integer.class).one());
            else h.createUpdate(sql).bindBean(movie).execute();
            h.createUpdate("DELETE FROM movie_genres WHERE movie_id=:id").bind("id", movie.getId()).execute();
            for (Integer genre : genres) h.createUpdate("INSERT INTO movie_genres (movie_id,genre_id) VALUES (:id,:genre)")
                    .bind("id", movie.getId()).bind("genre", genre).execute();
        });
    }

    public void deleteMovie(int id) {
        jdbi.useTransaction(h -> {
            h.createQuery("SELECT id FROM movies WHERE id=:id FOR UPDATE").bind("id", id).mapTo(Integer.class).findOne()
                    .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại."));
            if (count(h, "SELECT COUNT(*) FROM showtimes WHERE movie_id=:id", id) > 0)
                throw new IllegalArgumentException("Phim đã có lịch chiếu, không thể xóa. Có thể đóng lịch chiếu và ẩn phim.");
            h.createUpdate("DELETE FROM movie_genres WHERE movie_id=:id").bind("id", id).execute();
            h.createUpdate("DELETE FROM movies WHERE id=:id").bind("id", id).execute();
        });
    }

    public void saveShowtime(Showtime s, LocalDateTime start) {
        jdbi.useTransaction(h -> {
            Movie movie = h.createQuery("SELECT id,duration_minutes AS durationMinutes,status FROM movies WHERE id=:id FOR UPDATE")
                    .bind("id", s.getMovieId()).mapToBean(Movie.class).findOne()
                    .orElseThrow(() -> new IllegalArgumentException("Phim không tồn tại."));
            String room = h.createQuery("SELECT status FROM rooms WHERE id=:id FOR UPDATE")
                    .bind("id", s.getRoomId()).mapTo(String.class).findOne()
                    .orElseThrow(() -> new IllegalArgumentException("Phòng chiếu không tồn tại."));
            boolean changed = s.getId() == 0;
            if (s.getId() > 0) {
                Map<String,Object> old = h.createQuery("SELECT movie_id,room_id,start_time,price FROM showtimes WHERE id=:id FOR UPDATE")
                        .bind("id", s.getId()).mapToMap().findOne()
                        .orElseThrow(() -> new IllegalArgumentException("Lịch chiếu không tồn tại."));
                LocalDateTime oldStart = h.createQuery("SELECT start_time FROM showtimes WHERE id=:id").bind("id", s.getId()).mapTo(LocalDateTime.class).one();
                changed = ((Number)old.get("movie_id")).intValue() != s.getMovieId()
                        || ((Number)old.get("room_id")).intValue() != s.getRoomId()
                        || !oldStart.equals(start) || ((Number)old.get("price")).longValue() != s.getPrice();
                if (changed && count(h, "SELECT COUNT(*) FROM bookings WHERE showtime_id=:id", s.getId()) > 0)
                    throw new IllegalArgumentException("Lịch chiếu đã có đơn đặt vé: chỉ được thay đổi trạng thái.");
                if ("CANCELLED".equals(s.getStatus()) && count(h,
                        "SELECT COUNT(*) FROM bookings WHERE showtime_id=:id AND booking_status <> 'CANCELLED'", s.getId()) > 0)
                    throw new IllegalArgumentException("Hãy xử lý các đơn đặt vé trước khi hủy lịch chiếu.");
            }
            if ("OPEN".equals(s.getStatus())) {
                if (!"NOW_SHOWING".equals(movie.getStatus()) || !"ACTIVE".equals(room))
                    throw new IllegalArgumentException("Chỉ mở lịch cho phim đang chiếu và phòng đang hoạt động.");
                boolean future = h.createQuery("SELECT :start > NOW()") .bind("start", start).mapTo(Boolean.class).one();
                if (!future) throw new IllegalArgumentException("Lịch chiếu mở phải bắt đầu trong tương lai.");
            }
            if (!"CANCELLED".equals(s.getStatus()) && (changed || "OPEN".equals(s.getStatus()))) {
                int overlap = h.createQuery("""
                        SELECT COUNT(*) FROM showtimes st JOIN movies m ON m.id=st.movie_id
                        WHERE st.room_id=:room AND st.id<>:id AND st.status<>'CANCELLED'
                        AND st.start_time < :end
                        AND DATE_ADD(st.start_time, INTERVAL m.duration_minutes MINUTE) > :start
                        """).bind("room", s.getRoomId()).bind("id", s.getId()).bind("start", start)
                        .bind("end", start.plusMinutes(movie.getDurationMinutes())).mapTo(Integer.class).one();
                if (overlap > 0) throw new IllegalArgumentException("Lịch chiếu bị trùng thời gian trong cùng phòng.");
            }
            String sql = s.getId() == 0 ? """
                    INSERT INTO showtimes (movie_id,room_id,start_time,price,status) VALUES (:movieId,:roomId,:start,:price,:status)
                    """ : "UPDATE showtimes SET movie_id=:movieId,room_id=:roomId,start_time=:start,price=:price,status=:status WHERE id=:id";
            h.createUpdate(sql).bindBean(s).bind("start", start).execute();
        });
    }
    public void deleteShowtime(int id) {
        jdbi.useTransaction(h -> {
            h.createQuery("SELECT id FROM showtimes WHERE id=:id FOR UPDATE").bind("id", id).mapTo(Integer.class).findOne()
                    .orElseThrow(() -> new IllegalArgumentException("Lịch chiếu không tồn tại."));
            if (count(h, "SELECT COUNT(*) FROM bookings WHERE showtime_id=:id", id) > 0)
                throw new IllegalArgumentException("Lịch chiếu đã có đơn đặt vé, không thể xóa.");
            h.createUpdate("DELETE FROM showtimes WHERE id=:id").bind("id", id).execute();
        });
    }
    private int count(Handle h, String sql, int id) {
        return h.createQuery(sql).bind("id", id).mapTo(Integer.class).one();
    }
}
