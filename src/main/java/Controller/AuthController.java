package Controller;

import Model.User;
import Service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/logout", "/register"})
public class AuthController extends HttpServlet {
    private final AuthService authService;
    public AuthController() { this(new AuthService()); }
    public AuthController(AuthService authService) { this.authService = authService; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/register".equals(path)) {
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } else if ("/login".equals(path)) {
            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
        } else if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if ("/register".equals(request.getServletPath())) {
            try {
                authService.register(request.getParameter("fullName"), request.getParameter("email"),
                        request.getParameter("phone"), request.getParameter("password"), request.getParameter("confirmPassword"));
                response.sendRedirect(request.getContextPath() + "/login?registered=1");
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            } catch (RuntimeException e) {
                log("Registration failed", e);
                request.setAttribute("error", "Không thể đăng ký lúc này. Vui lòng thử lại.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = authService.login(email, password);

        if (user == null) {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        request.changeSessionId();
        user.setPasswordHash(null);
        session.setAttribute("currentUser", user);

        if ("ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
