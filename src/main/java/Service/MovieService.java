package Service;

import Dao.MovieDAO;
import Model.Movie;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {
    private final MovieDAO movieDAO;

    public MovieService() {
        this(new MovieDAO());
    }

    public MovieService(MovieDAO movieDAO) {
        this.movieDAO = movieDAO;
    }

    public List<Movie> getNowShowingMovies() {
        return movieDAO.findAllNowShowing();
    }

    public List<Movie> getMovies(String keyword) {
        // UC03 - 3.1.4: Nếu không có keyword thì trả về danh sách phim đang chiếu
        if (keyword == null || keyword.trim().isEmpty()) {
            return getNowShowingMovies();
        }

        // UC03 - 3.1.4: Nếu có keyword thì chuyển sang luồng tìm kiếm phim
        return searchMovies(keyword);
    }

    public List<Movie> searchMovies(String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);

        // Thử tìm bình thường trước (có dấu, không phân biệt hoa thường)
        List<Movie> results = movieDAO.findMoviesByKeyword(normalizedKeyword);

        if (!results.isEmpty()) {
            return results;
        }

        // Không có kết quả → thử tìm không dấu trong Java
        String lowerNorm = stripAccents(normalizedKeyword).toLowerCase();
        List<Movie> allMovies = movieDAO.findAllForSearch();

        List<Movie> filtered = allMovies.stream()
                .filter(m -> stripAccents(m.getTitle()).toLowerCase().contains(lowerNorm)
                        || stripAccents(m.getGenreNames() != null ? m.getGenreNames() : "").toLowerCase().contains(lowerNorm))
                .collect(Collectors.toList());

        filtered.sort(Comparator.comparingInt(m -> {
            String t = stripAccents(m.getTitle()).toLowerCase();
            if (t.startsWith(lowerNorm)) return 0;
            if (t.contains(lowerNorm)) return 1;
            return 2;
        }));

        return filtered;
    }

    public String normalizeKeyword(String keyword) {
        if (keyword == null) return "";
        return keyword.trim().replaceAll("\\s+", " ");
    }

    private String stripAccents(String text) {
        if (text == null) return "";
        // Xử lý đ/Đ trước vì không decompose qua NFD
        String s = text.replace("đ", "d").replace("Đ", "D");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return s;
    }

    public List<String> getAllGenres() {
        return movieDAO.findAllGenres();
    }

    // UC04 - 4.1.4: MovieService gọi MovieDAO để truy vấn thông tin chi tiết phim theo id
    public Movie getMovieDetail(int id) {
        return movieDAO.findById(id);
    }
}
