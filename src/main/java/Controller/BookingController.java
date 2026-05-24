package Controller;

import Model.Seat;
import Model.Showtime;
import Model.User;
import Service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/booking"})
public class BookingController extends HttpServlet {
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // UC06 - 6.1.1: Browser/JSP gửi requestBooking(showtimeId) đến BookingController
        showBookingPage(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // UC06 - 6.1.14: booking.jsp gửi submitBooking(showtimeId, seatIds) đến BookingController
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        // UC06 - 6.1.2: BookingController thực hiện checkLogin()
        if (currentUser == null) {
            // UC06 - 6.1.2A.1 - 6.1.2A.2: Khách hàng chưa đăng nhập, chuyển đến trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // UC06 - 6.1.3: BookingController kiểm tra showtimeId
        Integer showtimeId = parsePositiveInt(request.getParameter("showtimeId"));

        if (showtimeId == null) {
            // UC06 - 6.1.3A.1 - 6.1.3A.3: showtimeId thiếu, rỗng hoặc sai định dạng
            response.sendRedirect(request.getContextPath() + "/showtimes");
            return;
        }

        String[] seatIdValues = request.getParameterValues("seatIds");
        List<Integer> seatIds = new ArrayList<>();

        if (seatIdValues != null) {
            for (String value : seatIdValues) {
                Integer seatId = parsePositiveInt(value);

                if (seatId != null) {
                    seatIds.add(seatId);
                }
            }
        }

        try {
            // UC06 - 6.1.15: BookingController gọi BookingService.createBooking(userId, showtimeId, seatIds)
            int bookingId = bookingService.createBooking(currentUser.getId(), showtimeId, seatIds);

            // UC06 - 6.1.23: BookingService trả về bookingId cho BookingController

            // UC06 - 6.1.24: BookingController redirectToPayment(bookingId)
            response.sendRedirect(request.getContextPath() + "/payment?bookingId=" + bookingId);

        } catch (RuntimeException e) {
            // UC06 - 6.1.16A / 6.1.19A / 6.1.20A / 6.1.21A:
            // Nhận lỗi từ Service/DAO và hiển thị lại trang đặt vé
            showBookingPage(request, response, e.getMessage());
        }
    }

    private void showBookingPage(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("currentUser");

        // UC06 - 6.1.2: BookingController thực hiện checkLogin()
        if (currentUser == null) {
            // UC06 - 6.1.2A.1 - 6.1.2A.2: Khách hàng chưa đăng nhập, chuyển đến trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // UC06 - 6.1.3: BookingController kiểm tra showtimeId
        Integer showtimeId = parsePositiveInt(request.getParameter("showtimeId"));

        if (showtimeId == null) {
            // UC06 - 6.1.3A.1 - 6.1.3A.3: showtimeId thiếu, rỗng hoặc sai định dạng
            response.sendRedirect(request.getContextPath() + "/showtimes");
            return;
        }

        // UC06 - 6.1.4: BookingController gọi BookingService.getShowtimeDetail(showtimeId)
        Showtime showtime = bookingService.getShowtimeDetail(showtimeId);

        if (showtime == null || !"OPEN".equalsIgnoreCase(showtime.getStatus())) {
            // UC06 - 6.1.6A.1 - 6.1.6A.3:
            // Suất chiếu không tồn tại hoặc không còn cho phép đặt vé
            response.sendRedirect(request.getContextPath() + "/showtimes");
            return;
        }

        // UC06 - 6.1.7: BookingController gọi BookingService.getSeatsByShowtime(showtimeId)
        List<Seat> seats = bookingService.getSeatsByShowtime(showtimeId);

        request.setAttribute("showtime", showtime);
        request.setAttribute("seats", seats);
        request.setAttribute("error", error);

        // UC06 - 6.1.10: BookingController thực hiện showBookingPage(showtime, seats)
        request.getRequestDispatcher("/booking.jsp")
                .forward(request, response);
    }

    private Integer parsePositiveInt(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            int result = Integer.parseInt(value.trim());
            return result > 0 ? result : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }
}