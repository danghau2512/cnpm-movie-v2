package Controller;

import Model.Movie;
import Model.Showtime;
import Service.MovieService;
import Service.ShowtimeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/showtimes"})
public class ShowtimeController extends HttpServlet {
    private final ShowtimeService showtimeService = new ShowtimeService();
    private final MovieService movieService = new MovieService();

    // UC05 - 4.7.2: Nhận request /showtimes từ khách hàng
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // UC05 - 4.7.3: Lấy các tham số lọc lịch chiếu từ request
        String movieId = request.getParameter("movieId");
        String showDate = request.getParameter("showDate");
        String genreName = request.getParameter("genreName");

        // UC05 - Cải tiến: Lấy danh sách phim và thể loại để hiển thị bộ lọc trên giao diện
        List<Movie> movies = movieService.getNowShowingMovies();
        List<String> genres = movieService.getAllGenres();

        // UC05 - 4.7.4: Gọi service để lấy danh sách lịch chiếu phù hợp
        List<Showtime> showtimes = showtimeService.getShowtimes(movieId, showDate, genreName);

        // UC05 - 4.7.10: Lưu dữ liệu vào request để gửi sang JSP
        request.setAttribute("showtimes", showtimes);
        request.setAttribute("movies", movies);
        request.setAttribute("genres", genres);
        request.setAttribute("movieId", movieId);
        request.setAttribute("showDate", showDate);
        request.setAttribute("genreName", genreName);

        // UC05 - 4.7.11: Chuyển tiếp sang trang showtimes.jsp để hiển thị lịch chiếu
        request.getRequestDispatcher("/showtimes.jsp")
                .forward(request, response);
    }
}
