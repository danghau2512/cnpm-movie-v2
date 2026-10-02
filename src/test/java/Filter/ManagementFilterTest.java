package Filter;

import Model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class ManagementFilterTest {
    @Test void anonymousAdminRedirectsAndCustomerCannotAccessAdmin() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class); HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class); FilterChain chain = mock(FilterChain.class);
        when(req.getSession()).thenReturn(session); when(req.getServletPath()).thenReturn("/admin/movies"); when(req.getContextPath()).thenReturn("/cinebook");
        new ManagementFilter().doFilter(req, res, chain);
        verify(res).sendRedirect("/cinebook/login"); verifyNoInteractions(chain);
        User user = new User(); user.setRole("CUSTOMER"); when(session.getAttribute("currentUser")).thenReturn(user);
        new ManagementFilter().doFilter(req, res, chain);
        verify(res).sendError(403); verifyNoInteractions(chain);
    }
    @Test void adminPostNeedsCsrfToken() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class); HttpServletResponse res = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class); FilterChain chain = mock(FilterChain.class); User user = new User(); user.setRole("ADMIN");
        when(req.getSession()).thenReturn(session); when(req.getServletPath()).thenReturn("/admin/bookings"); when(req.getMethod()).thenReturn("POST");
        when(session.getAttribute("currentUser")).thenReturn(user); when(session.getAttribute("csrfToken")).thenReturn("expected");
        when(req.getParameter("csrfToken")).thenReturn("wrong");
        new ManagementFilter().doFilter(req, res, chain); verifyNoInteractions(chain);
        when(req.getParameter("csrfToken")).thenReturn("expected");
        new ManagementFilter().doFilter(req, res, chain); verify(chain).doFilter(req, res);
    }
}
