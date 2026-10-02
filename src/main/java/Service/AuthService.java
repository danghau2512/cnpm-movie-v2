package Service;

import Dao.UserDAO;
import Model.User;
import Util.PasswordUtil;
import java.util.Locale;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() { this(new UserDAO()); }
    public AuthService(UserDAO userDAO) { this.userDAO = userDAO; }

    public void register(String fullName, String email, String phone, String password, String confirmation) {
        fullName = fullName == null ? "" : fullName.trim();
        email = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        phone = phone == null ? "" : phone.trim();
        if (fullName.isEmpty() || fullName.length() > 100)
            throw new IllegalArgumentException("Họ tên phải có từ 1 đến 100 ký tự.");
        if (email.length() > 100 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("Email không hợp lệ.");
        if (!phone.matches("0[0-9]{9}"))
            throw new IllegalArgumentException("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
        if (password == null || password.length() < 6 || password.length() > 128 || password.isBlank())
            throw new IllegalArgumentException("Mật khẩu phải có từ 6 đến 128 ký tự.");
        if (!password.equals(confirmation))
            throw new IllegalArgumentException("Xác nhận mật khẩu không khớp.");
        if (userDAO.findByEmail(email) != null)
            throw new IllegalArgumentException("Email đã được đăng ký.");
        User user = new User();
        user.setFullName(fullName); user.setEmail(email); user.setPhone(phone);
        user.setPasswordHash(PasswordUtil.hash(password));
        try {
            userDAO.create(user);
        } catch (RuntimeException e) {
            for (Throwable cause = e; cause != null; cause = cause.getCause()) {
                if (cause instanceof java.sql.SQLException sql && sql.getErrorCode() == 1062)
                    throw new IllegalArgumentException("Email đã được đăng ký.");
            }
            throw e;
        }
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        email = email.trim();

        if (email.isEmpty() || password.isEmpty()) {
            return null;
        }

        User user = userDAO.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            return null;
        }

        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            return null;
        }

        if (!user.getPasswordHash().startsWith("pbkdf2$")) {
            String hash = PasswordUtil.hash(password.trim());
            userDAO.upgradePassword(user.getId(), user.getPasswordHash(), hash);
            user.setPasswordHash(hash);
        }
        return user;
    }
}
