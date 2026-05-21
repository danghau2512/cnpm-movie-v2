package Controller;

import Model.Showtime;
import Service.ShowtimeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/showtimes"})
public class ShowtimeController extends HttpServlet {
    private final ShowtimeService showtimeService = new ShowtimeService();

    // UC05 - 4.7.2: Nhận request /showtimes từ khách hàng
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // UC05 - 4.7.3: Lấy movieId từ request nếu khách hàng xem lịch chiếu theo phim
        String movieId = request.getParameter("movieId");

        // UC05 - 4.7.4: Gọi service để lấy danh sách lịch chiếu phù hợp
        List<Showtime> showtimes = showtimeService.getShowtimes(movieId);

        // UC05 - 4.7.10: Lưu danh sách lịch chiếu vào request để gửi sang JSP
        request.setAttribute("showtimes", showtimes);
        request.setAttribute("movieId", movieId);

        // UC05 - 4.7.11: Chuyển tiếp sang trang showtimes.jsp để hiển thị lịch chiếu
        request.getRequestDispatcher("/showtimes.jsp")
                .forward(request, response);
    }
}