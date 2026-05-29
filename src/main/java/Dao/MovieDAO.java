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
        // UC03 - 3.1.7: Tìm phim theo tên hoặc thể loại, bao gồm cả COMING_SOON
        // Cải tiến: không phân biệt hoa thường (LOWER), ưu tiên kết quả khớp tên hơn thể loại
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
            WHERE m.status IN ('NOW_SHOWING', 'COMING_SOON')
            AND (
                LOWER(m.title) LIKE LOWER(:keyword)
                OR LOWER(COALESCE(gd.genreNames, '')) LIKE LOWER(:keyword)
            )
            ORDER BY
                CASE
                    WHEN LOWER(m.title) LIKE LOWER(:kwStart) THEN 1
                    WHEN LOWER(m.title) LIKE LOWER(:kwContain) THEN 2
                    ELSE 3
                END,
                m.release_date DESC
            """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("keyword", "%" + keyword + "%")
                        .bind("kwStart", keyword + "%")
                        .bind("kwContain", "%" + keyword + "%")
                        .registerRowMapper(BeanMapper.factory(Movie.class))
                        .mapTo(Movie.class)
                        .list()
        );
    }

    public List<String> findAllGenres() {
        String sql = """
            SELECT DISTINCT g.name
            FROM genres g
            JOIN movie_genres mg ON g.id = mg.genre_id
            JOIN movies m ON mg.movie_id = m.id
            WHERE m.status IN ('NOW_SHOWING', 'COMING_SOON')
            ORDER BY g.name
            """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .mapTo(String.class)
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
