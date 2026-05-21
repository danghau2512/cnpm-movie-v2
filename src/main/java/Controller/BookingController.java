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


    /**
     * UC06 - Đặt vé
     * UC06 - 6.1.0: Khách hàng chọn nút Đặt vé từ một suất chiếu.
     * UC06 - 6.1.1: Hệ thống kiểm tra trạng thái đăng nhập trước khi hiển thị trang đặt vé.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showBookingPage(request, response, null);
    }

    /**
     * UC06 - Đặt vé
     * UC06 - 6.1.8: Khách hàng bấm nút Xác nhận đặt vé.
     * UC06 - 6.1.9: BookingController gọi BookingService để kiểm tra dữ liệu và tạo đơn đặt vé.
     * UC06 - 6.1.16: Nếu tạo đơn thành công, chuyển khách hàng sang chức năng thanh toán.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // UC06 - 6.1.1: Kiểm tra trạng thái đăng nhập của khách hàng
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        if (currentUser == null) {
            // UC06 - 6.2.1: Nếu chưa đăng nhập thì chuyển khách hàng đến trang đăng nhập
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // UC06 - 6.1.8: Nhận showtimeId và danh sách ghế khách hàng gửi lên khi xác nhận đặt vé
        int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));
        String[] seatIdValues = request.getParameterValues("seatIds");

        List<Integer> seatIds = new ArrayList<>();

        if (seatIdValues != null) {
            for (String value : seatIdValues) {
                seatIds.add(Integer.parseInt(value));
            }
        }

        try {
            // UC06 - 6.1.9: Gọi BookingService để kiểm tra dữ liệu và tạo đơn đặt vé

            // UC07 - 7.1.0: Sau khi tạo booking thành công, chuyển sang chức năng thanh toán với bookingId
            int bookingId = bookingService.createBooking(currentUser.getId(), showtimeId, seatIds);
            // UC06 - 6.1.16: Tạo đơn thành công, chuyển sang use case Thanh toán
            response.sendRedirect(request.getContextPath() + "/payment?bookingId=" + bookingId);

        } catch (RuntimeException e) {
            // UC06 - 6.2.5 / 6.2.10: Nếu có lỗi thì hiển thị lại trang chọn ghế kèm thông báo
            showBookingPage(request, response, e.getMessage());
        }
    }
    /**
     * UC06 - Đặt vé
     * UC06 - 6.1.2: Lấy thông tin chi tiết suất chiếu.
     * UC06 - 6.1.4: Lấy danh sách ghế theo suất chiếu.
     * UC06 - 6.1.6: Hiển thị giao diện chọn ghế.
     */
    private void showBookingPage(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // UC06 - 6.1.0: Lấy mã suất chiếu được gửi từ nút Đặt vé
        String showtimeIdRaw = request.getParameter("showtimeId");

        if (showtimeIdRaw == null || showtimeIdRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/showtimes");
            return;
        }

        int showtimeId = Integer.parseInt(showtimeIdRaw);
        // UC06 - 6.1.2: Gọi BookingService lấy thông tin chi tiết suất chiếu
        Showtime showtime = bookingService.getShowtimeDetail(showtimeId);
        // UC06 - 6.1.4: Gọi BookingService lấy danh sách ghế và trạng thái ghế
        List<Seat> seats = bookingService.getSeatsByShowtime(showtimeId);

        if (showtime == null) {
            // UC06 - 6.2.15: Suất chiếu không hợp lệ thì chuyển về trang lịch chiếu
            response.sendRedirect(request.getContextPath() + "/showtimes");
            return;
        }

        request.setAttribute("showtime", showtime);
        request.setAttribute("seats", seats);
        request.setAttribute("error", error);
        // UC06 - 6.1.6: Hiển thị trang chọn ghế
        request.getRequestDispatcher("/booking.jsp")
                .forward(request, response);
    }
}