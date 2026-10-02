package Service;

import Dao.AdminDAO;
import Model.Movie;
import Model.Showtime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class AdminService {
    private final AdminDAO dao;
    public AdminService() { this(new AdminDAO()); }
    public AdminService(AdminDAO dao) { this.dao = dao; }
    public List<Movie> getMovies() { return dao.movies(); }
    public List<Showtime> getShowtimes() { return dao.showtimes(); }
    public List<Map<String,Object>> getGenres() { return dao.genres(); }
    public List<Map<String,Object>> getRooms() { return dao.rooms(); }
    public List<Integer> getMovieGenres(int id) { return dao.movieGenres(id); }
    public void deleteMovie(int id) { requireId(id); dao.deleteMovie(id); }
    public void deleteShowtime(int id) { requireId(id); dao.deleteShowtime(id); }

    public void saveMovie(Movie movie, List<Integer> genres) {
        if (movie.getId() < 0) throw new IllegalArgumentException("Mã phim không hợp lệ.");
        movie.setTitle(text(movie.getTitle(), "Tên phim", 150, true));
        movie.setAgeRating(text(movie.getAgeRating(), "Giới hạn độ tuổi", 20, true));
        movie.setShortDescription(text(movie.getShortDescription(), "Mô tả ngắn", 255, false));
        movie.setDescription(text(movie.getDescription(), "Nội dung", 16000, false));
        movie.setPosterUrl(url(movie.getPosterUrl())); movie.setTrailerUrl(url(movie.getTrailerUrl()));
        if (movie.getDurationMinutes() < 1 || movie.getDurationMinutes() > 600)
            throw new IllegalArgumentException("Thời lượng phải từ 1 đến 600 phút.");
        if (!List.of("NOW_SHOWING", "COMING_SOON", "HIDDEN").contains(movie.getStatus() == null ? "" : movie.getStatus()))
            throw new IllegalArgumentException("Trạng thái phim không hợp lệ.");
        try {
            movie.setReleaseDate(movie.getReleaseDate() == null || movie.getReleaseDate().isBlank()
                    ? null : LocalDate.parse(movie.getReleaseDate()).toString());
        } catch (DateTimeParseException e) { throw new IllegalArgumentException("Ngày khởi chiếu không hợp lệ."); }
        if (genres == null || genres.stream().anyMatch(id -> id == null || id <= 0))
            throw new IllegalArgumentException("Thể loại không hợp lệ.");
        dao.saveMovie(movie, genres.stream().distinct().toList());
    }

    public void saveShowtime(Showtime s) {
        if (s.getId() < 0) throw new IllegalArgumentException("Mã lịch chiếu không hợp lệ.");
        requireId(s.getMovieId()); requireId(s.getRoomId());
        if (s.getPrice() < 1 || s.getPrice() > 9999999999L)
            throw new IllegalArgumentException("Giá vé phải từ 1 đến 9.999.999.999 VNĐ.");
        if (!List.of("OPEN", "CLOSED", "CANCELLED").contains(s.getStatus() == null ? "" : s.getStatus()))
            throw new IllegalArgumentException("Trạng thái lịch chiếu không hợp lệ.");
        LocalDateTime start;
        try { start = LocalDateTime.parse(s.getShowDate() + "T" + s.getShowTime()); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Ngày giờ chiếu không hợp lệ."); }
        dao.saveShowtime(s, start);
    }
    private void requireId(int id) { if (id <= 0) throw new IllegalArgumentException("Mã dữ liệu không hợp lệ."); }
    private String text(String value, String label, int max, boolean required) {
        value = value == null ? "" : value.trim();
        if ((required && value.isEmpty()) || value.length() > max)
            throw new IllegalArgumentException(label + " không hợp lệ (tối đa " + max + " ký tự).");
        return value;
    }
    private String url(String value) {
        value = text(value, "URL", 255, false);
        if (!value.isEmpty()) {
            try {
                java.net.URI uri = java.net.URI.create(value);
                if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme())) || uri.getHost() == null)
                    throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) { throw new IllegalArgumentException("URL phải bắt đầu bằng http:// hoặc https:// và có tên miền hợp lệ."); }
        }
        return value;
    }
}
