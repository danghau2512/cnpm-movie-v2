package Util;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class VnpayUtil {

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);

            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();

            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo chữ ký VNPay", e);
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");

        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
    // UC07 - 7.1.9: Ký dữ liệu request và tạo URL thanh toán VNPay Sandbox
    public static String buildPaymentUrl(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);

            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                    query.append("&");
                }

                hashData.append(fieldName)
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }

        String secureHash = hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashData.toString());

        return VnpayConfig.VNP_PAY_URL + "?" + query + "&vnp_SecureHash=" + secureHash;
    }
    // UC07 - 7.1.11: Kiểm tra chữ ký dữ liệu callback trả về từ VNPay Sandbox
    public static boolean verifyReturnUrl(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");

        if (vnpSecureHash == null || vnpSecureHash.isEmpty()) {
            return false;
        }

        Map<String, String> data = new HashMap<>(params);
        data.remove("vnp_SecureHash");
        data.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(data.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = data.get(fieldName);

            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                }

                hashData.append(fieldName)
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }

        String calculatedHash = hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashData.toString());

        return calculatedHash.equalsIgnoreCase(vnpSecureHash);
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return formatter.format(new Date());
    }
}