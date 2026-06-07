package Service;

import Dao.MovieDAO;
import Model.Movie;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {
    private final MovieDAO movieDAO = new MovieDAO();

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
        // UC03 - 3.1.5: MovieService nhận yêu cầu tìm kiếm phim từ SearchController

        // UC03 - 3.1.6: Chuẩn hóa keyword – trim khoảng trắng, gộp nhiều space liên tiếp
        String normalizedKeyword = normalizeKeyword(keyword);

        // UC03 - 3.1.7: Gọi MovieDAO.findMoviesByKeyword để tìm kiếm có dấu trong database
        List<Movie> results = movieDAO.findMoviesByKeyword(normalizedKeyword);

        // UC03 - 3.1.8: Nếu database trả về kết quả thì trả về luôn, không cần fallback
        if (!results.isEmpty()) {
            return results;
        }

        // UC03 - 3.1.8: Không có kết quả từ DB → fallback tìm không dấu bằng Java
        // Chuẩn hóa keyword: xử lý đ/Đ → d/D trước, sau đó NFD để bỏ dấu
        String lowerNorm = stripAccents(normalizedKeyword).toLowerCase();

        // UC03 - 3.1.9: Lấy toàn bộ danh sách phim từ DB để lọc phía Java
        List<Movie> allMovies = movieDAO.findAllForSearch();

        // UC03 - 3.1.9: Lọc phim theo tên hoặc thể loại sau khi chuẩn hóa không dấu
        List<Movie> filtered = allMovies.stream()
                .filter(m -> stripAccents(m.getTitle()).toLowerCase().contains(lowerNorm)
                        || stripAccents(m.getGenreNames() != null ? m.getGenreNames() : "").toLowerCase().contains(lowerNorm))
                .collect(Collectors.toList());

        // UC03 - 3.1.10: Sắp xếp kết quả: tên bắt đầu bằng keyword ưu tiên hơn tên chứa keyword
        filtered.sort(Comparator.comparingInt(m -> {
            String t = stripAccents(m.getTitle()).toLowerCase();
            if (t.startsWith(lowerNorm)) return 0;
            if (t.contains(lowerNorm)) return 1;
            return 2;
        }));

        // UC03 - 3.1.10: Trả kết quả tìm kiếm về SearchController
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

    public Movie getMovieDetail(int id) {
        return movieDAO.findById(id);
    }
}
