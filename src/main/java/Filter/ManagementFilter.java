package Filter;

import Model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns = {"/admin/*", "/booking-history", "/register", "/register.jsp"})
public class ManagementFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        boolean admin = request.getServletPath().startsWith("/admin/");
        boolean history = "/booking-history".equals(request.getServletPath());
        if ((admin || history) && user == null) {
            response.sendRedirect(request.getContextPath() + "/login"); return;
        }
        if (admin && !"ADMIN".equals(user.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN); return;
        }
        if (session.getAttribute("csrfToken") == null)
            session.setAttribute("csrfToken", UUID.randomUUID().toString());
        if ("POST".equals(request.getMethod()) && !session.getAttribute("csrfToken").equals(request.getParameter("csrfToken"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Phiên làm việc không hợp lệ. Vui lòng tải lại trang."); return;
        }
        response.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}
