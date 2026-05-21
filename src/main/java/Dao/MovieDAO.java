package Dao;

import Model.Movie;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.List;

public class MovieDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

    public List<Movie> findAllNowShowing() {
        String sql = """
            SELECT 
                m.id,
                m.title,
                m.duration_minutes AS durationMinutes,
                m.age_rating AS ageRating,
                m.short_description AS shortDescription,
                m.description,
                m.poster_url AS posterUrl,
                m.trailer_url AS trailerUrl,
                DATE_FORMAT(m.release_date, '%d/%m/%Y') AS releaseDate,
                m.status,
                COALESCE(gd.genreNames, 'Chưa phân loại') AS genreNames
            FROM movies m
            LEFT JOIN (
                SELECT 
                    mg.movie_id,
                    GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') AS genreNames
                FROM movie_genres mg
                JOIN genres g ON mg.genre_id = g.id
                GROUP BY mg.movie_id
            ) gd ON m.id = gd.movie_id
            WHERE m.status = 'NOW_SHOWING'
            ORDER BY m.id DESC
            """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .registerRowMapper(BeanMapper.factory(Movie.class))
                        .mapTo(Movie.class)
                        .list()
        );
    }

    public List<Movie> findMoviesByKeyword(String keyword) {
        // UC03 - 3.1.7: Tạo câu truy vấn tìm phim đang chiếu theo tên phim hoặc thể loại
        String sql = """
            SELECT 
                m.id,
                m.title,
                m.duration_minutes AS durationMinutes,
                m.age_rating AS ageRating,
                m.short_description AS shortDescription,
                m.description,
                m.poster_url AS posterUrl,
                m.trailer_url AS trailerUrl,
                DATE_FORMAT(m.release_date, '%d/%m/%Y') AS releaseDate,
                m.status,
                COALESCE(gd.genreNames, 'Chưa phân loại') AS genreNames
            FROM movies m
            LEFT JOIN (
                SELECT 
                    mg.movie_id,
                    GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') AS genreNames
                FROM movie_genres mg
                JOIN genres g ON mg.genre_id = g.id
                GROUP BY mg.movie_id
            ) gd ON m.id = gd.movie_id
            WHERE m.status = 'NOW_SHOWING'
            AND (
                m.title LIKE :keyword
                OR gd.genreNames LIKE :keyword
            )
            ORDER BY m.id DESC
            """;

        return jdbi.withHandle(handle ->
                // UC03 - 3.1.7: Bind keyword bằng tham số để tránh nối chuỗi SQL trực tiếp
                handle.createQuery(sql)
                        .bind("keyword", "%" + keyword + "%")
                        .registerRowMapper(BeanMapper.factory(Movie.class))
                        .mapTo(Movie.class)
                        // UC03 - 3.1.8: Nhận danh sách phim từ Database và map sang List<Movie>
                        .list()
        );
    }

    public Movie findById(int id) {
        String sql = """
            SELECT 
                m.id,
                m.title,
                m.duration_minutes AS durationMinutes,
                m.age_rating AS ageRating,
                m.short_description AS shortDescription,
                m.description,
                m.poster_url AS posterUrl,
                m.trailer_url AS trailerUrl,
                DATE_FORMAT(m.release_date, '%d/%m/%Y') AS releaseDate,
                m.status,
                COALESCE(gd.genreNames, 'Chưa phân loại') AS genreNames
            FROM movies m
            LEFT JOIN (
                SELECT 
                    mg.movie_id,
                    GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ') AS genreNames
                FROM movie_genres mg
                JOIN genres g ON mg.genre_id = g.id
                GROUP BY mg.movie_id
            ) gd ON m.id = gd.movie_id
            WHERE m.id = :id
            """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .registerRowMapper(BeanMapper.factory(Movie.class))
                        .mapTo(Movie.class)
                        .findOne()
                        .orElse(null)
        );
    }
}
