package Service;

import Dao.PaymentDAO;
import Model.PaymentInfo;
import Util.VnpayConfig;
import Util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PaymentService {
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public PaymentInfo getPaymentInfo(int bookingId) {
        return paymentDAO.findPaymentInfo(bookingId);
    }
    // UC07 - 7.2.7: Xử lý nghiệp vụ thanh toán tại quầy
    public void processPayAtCounter(int bookingId) {
        paymentDAO.payAtCounter(bookingId);
    }

    public String createVnpayPaymentUrl(int bookingId, HttpServletRequest request) {
        // UC07 - 7.1.7: Lấy thông tin booking để chuẩn bị thanh toán VNPay Sandbox
        PaymentInfo info = paymentDAO.findPaymentInfo(bookingId);

        if (info == null) {
            throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y thÃ´ng tin Ä‘áº·t vÃ©.");
        }
// UC07 - 7.1.8: Tạo payment tạm thời với trạng thái PENDING trước khi chuyển sang VNPay
        paymentDAO.createVnpayPendingPayment(bookingId);
// UC07 - 7.1.7: Tạo returnUrl để VNPay redirect kết quả về hệ thống
        String returnUrl = request.getScheme() + "://"
                + request.getServerName()
                + ":"
                + request.getServerPort()
                + request.getContextPath()
                + "/vnpay-return";

        BigDecimal totalAmount = info.getTotalAmount();

        long amount = totalAmount.longValue() * 100;
        String vnpTxnRef = bookingId + "_" + System.currentTimeMillis();
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", VnpayConfig.VNP_VERSION);
        params.put("vnp_Command", VnpayConfig.VNP_COMMAND);
        params.put("vnp_TmnCode", VnpayConfig.VNP_TMN_CODE);
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_CurrCode", VnpayConfig.VNP_CURR_CODE);
        params.put("vnp_TxnRef", vnpTxnRef);
        params.put("vnp_OrderInfo", "Thanh toan ve xem phim " + info.getBookingCode());
        params.put("vnp_OrderType", VnpayConfig.VNP_ORDER_TYPE);
        params.put("vnp_Locale", VnpayConfig.VNP_LOCALE);
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));
        params.put("vnp_CreateDate", VnpayUtil.getCurrentDate());
// UC07 - 7.1.9: Tạo tham số ký số và sinh URL thanh toán VNPay Sandbox
        return VnpayUtil.buildPaymentUrl(params);
    }

    public int handleVnpayReturn(Map<String, String> params) {
        // UC07 - 7.1.11: Xác minh chữ ký callback từ VNPay Sandbox
        boolean validSignature = VnpayUtil.verifyReturnUrl(params);

        if (!validSignature) {
            throw new RuntimeException("Sai chá»¯ kÃ½ VNPay.");
        }
// UC07 - 7.1.11: Lấy bookingId và trạng thái giao dịch từ dữ liệu VNPay trả về

// UC07 - 7.1.12: Nếu giao dịch thành công thì cập nhật payment SUCCESS và booking CONFIRMED
// UC07 - 7.2.15: Nếu giao dịch thất bại thì cập nhật payment FAILED và booking CANCELLED
        String txnRef = params.get("vnp_TxnRef");
        int bookingId = Integer.parseInt(txnRef.split("_")[0]);
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String transactionCode = params.get("vnp_TransactionNo");

        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            paymentDAO.confirmVnpayPayment(bookingId, transactionCode);
        } else {
            paymentDAO.failVnpayPayment(bookingId, transactionCode);
        }

        return bookingId;
    }
}
