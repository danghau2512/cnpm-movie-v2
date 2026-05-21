package Service;

import Dao.UserDAO;
import Model.User;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        email = email.trim();
        password = password.trim();

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

        if (!password.equals(user.getPasswordHash())) {
            return null;
        }

        return user;
    }
}