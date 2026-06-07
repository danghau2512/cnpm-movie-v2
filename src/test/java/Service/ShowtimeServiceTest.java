package Service;

import Model.Showtime;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// UC05 - Development Testing: kiểm tra nghiệp vụ lọc lịch chiếu trong ShowtimeService
class ShowtimeServiceTest {

    private static Jdbi jdbi;
    private ShowtimeService showtimeService;

    @BeforeAll
    static void beforeAll() {
        jdbi = JdbiConnector.getJdbi();
    }

    @BeforeEach
    void setUp() {
        showtimeService = new ShowtimeService();
    }

    @Test
    @DisplayName("Issue #23 - Service chỉ trả về các suất chiếu đang mở và chưa quá giờ")
    void getShowtimes_ShouldReturnOpenShowtimesOnly() {
        List<Showtime> showtimes = showtimeService.getShowtimes(null, null, null);

        assertNotNull(showtimes);
        showtimes.forEach(showtime -> assertEquals("OPEN", showtime.getStatus()));
    }

    @Test
    @DisplayName("Issue #39 - Service lọc lịch chiếu theo ngày")
    void getShowtimes_WithShowDate_ShouldReturnOnlySelectedDate() {
        Optional<String> dateOptional = findAvailableShowDate();
        assumeTrue(dateOptional.isPresent(), "Bỏ qua test vì database không có suất chiếu OPEN trong tương lai.");

        String showDate = dateOptional.get();
        String expectedDateText = convertToVietnameseDate(showDate);

        List<Showtime> showtimes = showtimeService.getShowtimes(null, showDate, null);

        assertFalse(showtimes.isEmpty());
        showtimes.forEach(showtime -> assertEquals(expectedDateText, showtime.getShowDate()));
    }

    @Test
    @DisplayName("Issue #41 - Service lọc lịch chiếu theo tên phim thông qua movieId")
    void getShowtimes_WithMovieId_ShouldReturnOnlySelectedMovie() {
        Optional<Integer> movieIdOptional = findAvailableMovieId();
        assumeTrue(movieIdOptional.isPresent(), "Bỏ qua test vì database không có phim có suất chiếu OPEN trong tương lai.");

        int movieId = movieIdOptional.get();

        List<Showtime> showtimes = showtimeService.getShowtimes(String.valueOf(movieId), null, null);

        assertFalse(showtimes.isEmpty());
        showtimes.forEach(showtime -> assertEquals(movieId, showtime.getMovieId()));
    }

    @Test
    @DisplayName("Issue #41 - Service xử lý movieId sai định dạng mà không phát sinh lỗi")
    void getShowtimes_WithInvalidMovieId_ShouldNotThrowException() {
        assertDoesNotThrow(() -> showtimeService.getShowtimes("abc", null, null));
        assertDoesNotThrow(() -> showtimeService.getShowtimes("0", null, null));
        assertDoesNotThrow(() -> showtimeService.getShowtimes("-1", null, null));
    }

    @Test
    @DisplayName("Issue #41 - Service lọc lịch chiếu theo thể loại phim")
    void getShowtimes_WithGenreName_ShouldReturnShowtimesOfSelectedGenre() {
        Optional<String> genreOptional = findAvailableGenreName();
        assumeTrue(genreOptional.isPresent(), "Bỏ qua test vì database không có thể loại gắn với suất chiếu OPEN trong tương lai.");

        String genreName = genreOptional.get();

        List<Showtime> showtimes = showtimeService.getShowtimes(null, null, genreName);

        assertFalse(showtimes.isEmpty());
        showtimes.forEach(showtime -> assertTrue(movieHasGenre(showtime.getMovieId(), genreName)));
    }

    private Optional<String> findAvailableShowDate() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT DATE(start_time)
                                FROM showtimes
                                WHERE status = 'OPEN'
                                AND start_time >= NOW()
                                ORDER BY start_time ASC
                                LIMIT 1
                                """)
                        .mapTo(String.class)
                        .findOne()
        );
    }

    private Optional<Integer> findAvailableMovieId() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT movie_id
                                FROM showtimes
                                WHERE status = 'OPEN'
                                AND start_time >= NOW()
                                ORDER BY start_time ASC
                                LIMIT 1
                                """)
                        .mapTo(Integer.class)
                        .findOne()
        );
    }

    private Optional<String> findAvailableGenreName() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT g.name
                                FROM showtimes s
                                JOIN movie_genres mg ON s.movie_id = mg.movie_id
                                JOIN genres g ON mg.genre_id = g.id
                                WHERE s.status = 'OPEN'
                                AND s.start_time >= NOW()
                                ORDER BY s.start_time ASC
                                LIMIT 1
                                """)
                        .mapTo(String.class)
                        .findOne()
        );
    }

    private boolean movieHasGenre(int movieId, String genreName) {
        Integer count = jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT COUNT(*)
                                FROM movie_genres mg
                                JOIN genres g ON mg.genre_id = g.id
                                WHERE mg.movie_id = :movieId
                                AND g.name = :genreName
                                """)
                        .bind("movieId", movieId)
                        .bind("genreName", genreName)
                        .mapTo(Integer.class)
                        .one()
        );

        return count > 0;
    }

    private String convertToVietnameseDate(String mysqlDate) {
        String[] parts = mysqlDate.split("-");
        return parts[2] + "/" + parts[1] + "/" + parts[0];
    }
}
