package Controller;

import Model.User;
import Service.BookingManagementService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/booking-history")
public class BookingHistoryController extends HttpServlet {
    private final BookingManagementService service;
    public BookingHistoryController() { this(new BookingManagementService()); }
    public BookingHistoryController(BookingManagementService service) { this.service = service; }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("currentUser");
        if (user == null) { res.sendRedirect(req.getContextPath() + "/login"); return; }
        try { req.setAttribute("bookings", service.getHistory(user.getId())); }
        catch (RuntimeException e) {
            log("Booking history load failed", e);
            req.setAttribute("error", "Không thể tải lịch sử đặt vé. Vui lòng thử lại.");
        }
        req.getRequestDispatcher("/WEB-INF/views/booking-history.jsp").forward(req, res);
    }
}
