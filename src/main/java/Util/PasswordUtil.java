package Util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final int ITERATIONS = 210000;
    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS));
    }

    public static boolean matches(String password, String stored) {
        if (password == null || stored == null) return false;
        // Tương thích tài khoản cũ; AuthService nâng cấp hash sau khi đăng nhập thành công.
        if (!stored.startsWith("pbkdf2$")) return password.trim().equals(stored);
        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 1 || iterations > 1000000) return false;
            return MessageDigest.isEqual(Base64.getDecoder().decode(parts[3]),
                    derive(password, Base64.getDecoder().decode(parts[2]), iterations));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Không thể mã hóa mật khẩu.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
