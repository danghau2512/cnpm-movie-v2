package Controller;

import Model.PaymentInfo;
import Model.User;
import Service.PaymentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/payment", "/payment-result", "/vnpay-return"})
public class PaymentController extends HttpServlet {
    private final PaymentService paymentService = new PaymentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/vnpay-return".equals(path)) {
            handleVnpayReturn(request, response);
            return;
        }
// UC07 - 7.1.1: Kiểm tra khách hàng đã đăng nhập trước khi truy cập chức năng thanh toán
        User currentUser = (User) request.getSession().getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
// UC07 - 7.1.2 + 7.1.4: Lấy thông tin thanh toán của booking và kiểm tra quyền sở hữu đơn
        PaymentInfo paymentInfo = paymentService.getPaymentInfo(bookingId);

        if (paymentInfo == null || paymentInfo.getUserId() != currentUser.getId()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.setAttribute("paymentInfo", paymentInfo);

        if ("/payment".equals(path)) {
            // UC07 - 7.1.4: Nếu booking hợp lệ thì hiển thị trang payment.jsp
            request.getRequestDispatcher("/payment.jsp")
                    .forward(request, response);
        } else {
            request.getRequestDispatcher("/payment-result.jsp")
                    .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        User currentUser = (User) request.getSession().getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer bookingId = parseBookingId(request.getParameter("bookingId"));

        if (bookingId == null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }        String method = request.getParameter("paymentMethod");

        PaymentInfo paymentInfo = paymentService.getPaymentInfo(bookingId);

        if (paymentInfo == null || paymentInfo.getUserId() != currentUser.getId()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
// UC07 - 7.2.6: Khách hàng chọn phương thức thanh toán tại quầy
// UC07 - 7.2.7: Gọi service xử lý thanh toán tại quầy
// UC07 - 7.2.9: Chuyển sang trang kết quả thanh toán
        if ("PAY_AT_COUNTER".equals(method)) {
            paymentService.processPayAtCounter(bookingId);
            response.sendRedirect(request.getContextPath() + "/payment-result?bookingId=" + bookingId);
            return;
        }
// UC07 - 7.1.5: Khách hàng chọn phương thức thanh toán VNPay
// UC07 - 7.1.6: Controller nhận bookingId và paymentMethod từ form
// UC07 - 7.1.7: Gọi service tạo URL thanh toán VNPay Sandbox
// UC07 - 7.1.9: Redirect khách hàng sang cổng thanh toán test
        if ("VNPAY".equals(method)) {
            String paymentUrl = paymentService.createVnpayPaymentUrl(bookingId, request);
            response.sendRedirect(paymentUrl);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/payment?bookingId=" + bookingId);
    }
// UC07 - 7.1.10: Nhận callback từ VNPay Sandbox sau khi khách hàng hoàn tất thao tác thanh toán

    // UC07 - 7.1.12: Nếu xử lý callback thành công thì chuyển sang trang kết quả thanh toán
    private void handleVnpayReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Map<String, String> params = new HashMap<>();

        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });

        try {
            int bookingId = paymentService.handleVnpayReturn(params);
            response.sendRedirect(request.getContextPath() + "/payment-result?bookingId=" + bookingId);
        } catch (RuntimeException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
    private Integer parseBookingId(String bookingIdParam) {
        if (bookingIdParam == null || bookingIdParam.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(bookingIdParam);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}