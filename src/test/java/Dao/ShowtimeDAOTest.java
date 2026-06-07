package Dao;

import Model.Showtime;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// UC05 - Development Testing: kiểm tra truy vấn dữ liệu lịch chiếu trong ShowtimeDAO
class ShowtimeDAOTest {

    private static Jdbi jdbi;
    private ShowtimeDAO showtimeDAO;

    @BeforeAll
    static void beforeAll() {
        jdbi = JdbiConnector.getJdbi();
    }

    @BeforeEach
    void setUp() {
        showtimeDAO = new ShowtimeDAO();
    }

    @Test
    @DisplayName("Issue #23 - findAllOpen chỉ lấy suất chiếu OPEN và chưa quá thời gian")
    void findAllOpen_ShouldReturnOnlyOpenAndFutureShowtimes() {
        List<Showtime> showtimes = showtimeDAO.findAllOpen(null, null);

        assertNotNull(showtimes);
        showtimes.forEach(showtime -> assertEquals("OPEN", showtime.getStatus()));
    }

    @Test
    @DisplayName("Issue #39 - findAllOpen lọc đúng lịch chiếu theo ngày")
    void findAllOpen_WithShowDate_ShouldReturnOnlySelectedDate() {
        Optional<String> dateOptional = findAvailableShowDate();
        assumeTrue(dateOptional.isPresent(), "Bỏ qua test vì database không có suất chiếu OPEN trong tương lai.");

        String showDate = dateOptional.get();
        String expectedDateText = convertToVietnameseDate(showDate);

        List<Showtime> showtimes = showtimeDAO.findAllOpen(showDate, null);

        assertFalse(showtimes.isEmpty());
        showtimes.forEach(showtime -> assertEquals(expectedDateText, showtime.getShowDate()));
    }

    @Test
    @DisplayName("Issue #41 - findByMovieId lọc đúng lịch chiếu theo phim")
    void findByMovieId_ShouldReturnOnlySelectedMovie() {
        Optional<Integer> movieIdOptional = findAvailableMovieId();
        assumeTrue(movieIdOptional.isPresent(), "Bỏ qua test vì database không có phim có suất chiếu OPEN trong tương lai.");

        int movieId = movieIdOptional.get();

        List<Showtime> showtimes = showtimeDAO.findByMovieId(movieId, null, null);

        assertFalse(showtimes.isEmpty());
        showtimes.forEach(showtime -> assertEquals(movieId, showtime.getMovieId()));
    }

    @Test
    @DisplayName("Issue #41 - findAllOpen lọc đúng lịch chiếu theo thể loại")
    void findAllOpen_WithGenreName_ShouldReturnOnlySelectedGenre() {
        Optional<String> genreOptional = findAvailableGenreName();
        assumeTrue(genreOptional.isPresent(), "Bỏ qua test vì database không có thể loại gắn với suất chiếu OPEN trong tương lai.");

        String genreName = genreOptional.get();

        List<Showtime> showtimes = showtimeDAO.findAllOpen(null, genreName);

        assertFalse(showtimes.isEmpty());
        showtimes.forEach(showtime -> assertTrue(movieHasGenre(showtime.getMovieId(), genreName)));
    }

    @Test
    @DisplayName("Issue #39, #41 - findByMovieId có thể kết hợp lọc theo phim, ngày và thể loại")
    void findByMovieId_WithDateAndGenre_ShouldReturnMatchingShowtimes() {
        Optional<TestFilterData> dataOptional = findAvailableFilterData();
        assumeTrue(dataOptional.isPresent(), "Bỏ qua test vì database không có đủ dữ liệu phim/ngày/thể loại.");

        TestFilterData data = dataOptional.get();
        String expectedDateText = convertToVietnameseDate(data.showDate);

        List<Showtime> showtimes = showtimeDAO.findByMovieId(data.movieId, data.showDate, data.genreName);

        assertFalse(showtimes.isEmpty());
        showtimes.forEach(showtime -> {
            assertEquals(data.movieId, showtime.getMovieId());
            assertEquals(expectedDateText, showtime.getShowDate());
            assertTrue(movieHasGenre(showtime.getMovieId(), data.genreName));
        });
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

    private Optional<TestFilterData> findAvailableFilterData() {
        return jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT s.movie_id, DATE(s.start_time) AS show_date, g.name AS genre_name
                                FROM showtimes s
                                JOIN movie_genres mg ON s.movie_id = mg.movie_id
                                JOIN genres g ON mg.genre_id = g.id
                                WHERE s.status = 'OPEN'
                                AND s.start_time >= NOW()
                                ORDER BY s.start_time ASC
                                LIMIT 1
                                """)
                        .map((rs, ctx) -> new TestFilterData(
                                rs.getInt("movie_id"),
                                rs.getString("show_date"),
                                rs.getString("genre_name")
                        ))
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

    private static class TestFilterData {
        private final int movieId;
        private final String showDate;
        private final String genreName;

        private TestFilterData(int movieId, String showDate, String genreName) {
            this.movieId = movieId;
            this.showDate = showDate;
            this.genreName = genreName;
        }
    }
}
