package Util;

import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class VnpayUtilTest {

    @Test
    void hmacSHA512_WithValidData_ShouldReturnHashString() {
        String hash = VnpayUtil.hmacSHA512("secret-key", "bookingId=15&amount=180000");

        assertNotNull(hash);
        assertEquals(128, hash.length());
    }

    @Test
    void hmacSHA512_WithSameInput_ShouldReturnSameHash() {
        String hash1 = VnpayUtil.hmacSHA512("secret-key", "vnp_TxnRef=15");
        String hash2 = VnpayUtil.hmacSHA512("secret-key", "vnp_TxnRef=15");

        assertEquals(hash1, hash2);
    }

    @Test
    void buildPaymentUrl_WithValidParams_ShouldReturnVnpayUrlWithSecureHash() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", "TESTCODE");
        params.put("vnp_Amount", "18000000");
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", "15_20260607103000");
        params.put("vnp_OrderInfo", "Thanh toan ve xem phim BK001");
        params.put("vnp_ReturnUrl", "http://localhost:8080/vnpay-return");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_CreateDate", "20260607103000");

        String paymentUrl = VnpayUtil.buildPaymentUrl(params);

        assertTrue(paymentUrl.startsWith(VnpayConfig.VNP_PAY_URL));
        assertTrue(paymentUrl.contains("vnp_TxnRef=15_20260607103000"));
        assertTrue(paymentUrl.contains("vnp_SecureHash="));
    }

    @Test
    void verifyReturnUrl_WithValidSignature_ShouldReturnTrue() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "15_20260607103000");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00");
        params.put("vnp_TransactionNo", "999999");

        String hashData = buildHashData(params);
        String secureHash = VnpayUtil.hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashData);

        params.put("vnp_SecureHash", secureHash);

        assertTrue(VnpayUtil.verifyReturnUrl(params));
    }

    @Test
    void verifyReturnUrl_WithInvalidSignature_ShouldReturnFalse() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "15_20260607103000");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00");
        params.put("vnp_TransactionNo", "999999");
        params.put("vnp_SecureHash", "wrong-signature");

        assertFalse(VnpayUtil.verifyReturnUrl(params));
    }

    @Test
    void verifyReturnUrl_WithoutSignature_ShouldReturnFalse() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "15_20260607103000");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00");

        assertFalse(VnpayUtil.verifyReturnUrl(params));
    }

    @Test
    void verifyReturnUrl_WithSecureHashType_ShouldIgnoreSecureHashType() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "15_20260607103000");
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00");
        params.put("vnp_TransactionNo", "999999");
        params.put("vnp_SecureHashType", "HmacSHA512");

        Map<String, String> dataForHash = new HashMap<>(params);
        dataForHash.remove("vnp_SecureHashType");

        String hashData = buildHashData(dataForHash);
        String secureHash = VnpayUtil.hmacSHA512(VnpayConfig.VNP_HASH_SECRET, hashData);

        params.put("vnp_SecureHash", secureHash);

        assertTrue(VnpayUtil.verifyReturnUrl(params));
    }

    @Test
    void getCurrentDate_ShouldReturnFourteenDigitString() {
        String currentDate = VnpayUtil.getCurrentDate();

        assertNotNull(currentDate);
        assertTrue(currentDate.matches("\\d{14}"));
    }

    private String buildHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);

            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                }

                hashData.append(fieldName)
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }

        return hashData.toString();
    }
}