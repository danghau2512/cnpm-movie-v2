package Controller;

import Model.Movie;
import Service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/movies", "/movie-detail"})
public class MovieController extends HttpServlet {
    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();

        if ("/movies".equals(path)) {
            showMovieList(request, response);
        } else if ("/movie-detail".equals(path)) {
            showMovieDetail(request, response);
        }
    }

    private void showMovieList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String detailMessage = request.getParameter("detailMessage");

        // UC04 - A1/A2/A3/A7: Hiển thị thông báo khi người dùng truy cập chi tiết phim không hợp lệ
        List<Movie> movies = movieService.getMovies(keyword);

        request.setAttribute("movies", movies);
        request.setAttribute("keyword", keyword);

        if ("missing_id".equals(detailMessage)) {
            request.setAttribute("message", "Vui lòng chọn phim từ danh sách.");
        } else if ("invalid_id".equals(detailMessage)) {
            request.setAttribute("message", "Mã phim không hợp lệ.");
        } else if ("movie_not_available".equals(detailMessage)) {
            request.setAttribute("message", "Không tìm thấy phim hoặc phim không còn được hiển thị.");
        }
        request.getRequestDispatcher("/movies.jsp")
                .forward(request, response);
    }

    private void showMovieDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idRaw = request.getParameter("id");

        if (idRaw == null || idRaw.trim().isEmpty()) {
            // UC04 - A1: Thiếu id phim thì quay về danh sách và hiển thị thông báo
            response.sendRedirect(request.getContextPath() + "/movies?detailMessage=missing_id");
            return;
        }

        int id;
        try{
            id = Integer.parseInt(idRaw);
        } catch (NumberFormatException e){
            // UC04 - A2: id không hợp lệ thì quay về danh sách và hiển thị thông báo
            response.sendRedirect(request.getContextPath() + "/movies?detailMessage=invalid_id");
            return;
        }

        Movie movie = movieService.getMovieDetail(id);

        // UC04 - 4.1.7: Nếu phim không tồn tại hoặc không thuộc trạng thái được hiển thị thì quay về danh sách phim
        if (movie == null) {
            // UC04 - 4.1.7 + A3/A7: Phim không tồn tại hoặc không được hiển thị
            response.sendRedirect(request.getContextPath() + "/movies?detailMessage=movie_not_available");
            return;
        }

        request.setAttribute("movie", movie);

        request.getRequestDispatcher("/movie-detail.jsp")
                .forward(request, response);
    }
}