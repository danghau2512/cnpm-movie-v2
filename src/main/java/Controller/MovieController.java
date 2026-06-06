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


        List<Movie> movies = movieService.getMovies(keyword);

        request.setAttribute("movies", movies);
        request.setAttribute("keyword", keyword);

        // UC04 - A1/A2/A3/A7: Nhận detailMessage sau khi redirect từ trang chi tiết phim
        // và hiển thị thông báo lỗi tương ứng trên trang danh sách phim
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

        // UC04 - 4.1.1: MovieController tiếp nhận request và lấy tham số id từ URL
        String idRaw = request.getParameter("id");

        // Lấy keyword để khi quay lại danh sách vẫn giữ được kết quả tìm kiếm trước đó
        String keyword = request.getParameter("keyword");

        // UC04 - A1: Tham số id bị thiếu hoặc rỗng
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/movies?detailMessage=missing_id");
            return;
        }

        int id;
        try{
            // UC04 - 4.1.2: Chuyển id từ String sang số nguyên
            id = Integer.parseInt(idRaw);
        } catch (NumberFormatException e){
            // UC04 - A2: Tham số id không hợp lệ, không phải số
            response.sendRedirect(request.getContextPath() + "/movies?detailMessage=invalid_id");
            return;
        }

        // UC04 - 4.1.3: MovieController gọi MovieService để lấy chi tiết phim
        Movie movie = movieService.getMovieDetail(id);

        // UC04 - 4.1.7 + A3/A7:
        // Nếu phim không tồn tại hoặc không thuộc trạng thái NOW_SHOWING thì quay về danh sách phim
        if (movie == null) {
            response.sendRedirect(request.getContextPath() + "/movies?detailMessage=movie_not_available");
            return;
        }

        // UC04 - 4.1.8: Đặt dữ liệu phim vào request attribute và forward sang trang chi tiết
        // Bổ sung: giữ lại keyword nếu người dùng đi từ trang tìm kiếm để nút "Quay lại" hoạt động đúng
        request.setAttribute("movie", movie);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher("/movie-detail.jsp")
                .forward(request, response);
    }
}