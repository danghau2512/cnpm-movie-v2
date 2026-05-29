package Service;

import Dao.MovieDAO;
import Model.Movie;

import java.util.List;

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
        // UC03 - 3.1.5: Chuẩn hóa keyword trước khi gọi DAO tìm kiếm phim
        String normalizedKeyword = normalizeKeyword(keyword);

        // UC03 - 3.1.6: Gọi MovieDAO để tìm danh sách phim theo từ khóa đã chuẩn hóa
        return movieDAO.findMoviesByKeyword(normalizedKeyword);
    }

    public String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }

        // UC03 - 3.1.5: Loại bỏ khoảng trắng thừa ở đầu/cuối và gom nhiều khoảng trắng thành một khoảng trắng
        return keyword.trim().replaceAll("\\s+", " ");
    }

    public List<String> getAllGenres() {
        return movieDAO.findAllGenres();
    }

    public Movie getMovieDetail(int id) {
        return movieDAO.findById(id);
    }
}
