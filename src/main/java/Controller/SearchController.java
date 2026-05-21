package Controller;

import Model.Movie;
import Service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/search"})
public class SearchController extends HttpServlet {
    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        processSearch(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        processSearch(request, response);
    }

    private void processSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // UC03 - 3.1.2: Nhận request tìm kiếm phim từ header.jsp qua route /search
        String keyword = request.getParameter("keyword");

        // UC03 - 3.1.3: Kiểm tra keyword từ request trước khi gọi service xử lý tìm kiếm
        boolean hasKeywordParam = request.getParameterMap().containsKey("keyword");
        boolean invalidKeyword = hasKeywordParam && (keyword == null || keyword.trim().isEmpty());

        if (invalidKeyword) {
            // UC03 - 3.2.0: Keyword rỗng hoặc không hợp lệ thì trả thông báo yêu cầu nhập từ khóa
            request.setAttribute("message", "Vui lòng nhập từ khóa tìm kiếm phim.");
            request.setAttribute("keyword", "");
            request.setAttribute("movies", List.of());

            // UC03 - 3.2.1: Chuyển thông báo lỗi về giao diện để hiển thị cho khách hàng
            request.getRequestDispatcher("/movies.jsp")
                    .forward(request, response);
            return;
        }

        List<Movie> movies;

        if (!hasKeywordParam) {
            // UC03 - 3.1.4: Khi chưa có keyword, hệ thống hiển thị danh sách phim đang chiếu ban đầu
            movies = movieService.getNowShowingMovies();
            request.setAttribute("movies", movies);
            request.setAttribute("keyword", "");
            request.getRequestDispatcher("/movies.jsp")
                    .forward(request, response);
            return;
        }

        // UC03 - 3.1.4: Gọi MovieService để xử lý nghiệp vụ tìm kiếm phim theo keyword hợp lệ
        movies = movieService.searchMovies(keyword);

        request.setAttribute("movies", movies);
        request.setAttribute("keyword", keyword.trim());

        if (movies.isEmpty()) {
            // UC03 - 3.2.2: Không tìm thấy phim thì gửi thông báo không có kết quả sang JSP
            request.setAttribute("message", "Không tìm thấy phim phù hợp với từ khóa: " + keyword.trim());
        }

        // UC03 - 3.1.11: Chuyển danh sách phim tìm được sang JSP để hiển thị kết quả tìm kiếm
        request.getRequestDispatcher("/movies.jsp")
                .forward(request, response);
    }
}
