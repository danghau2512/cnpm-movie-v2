package Controller;

import Model.Movie;
import Service.MovieService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Test
    @DisplayName("UC04 - Thiếu id thì redirect về missing_id")
    void showMovieDetail_WhenMissingId_ShouldRedirectMissingId() throws Exception {
        MovieController controller = new MovieController(movieService);

        when(request.getServletPath()).thenReturn("/movie-detail");
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("keyword")).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        controller.doGet(request, response);

        verify(response).sendRedirect("/movies?detailMessage=missing_id");
        verifyNoInteractions(movieService);
    }

    @Test
    @DisplayName("UC04 - id không phải số thì redirect về invalid_id")
    void showMovieDetail_WhenInvalidId_ShouldRedirectInvalidId() throws Exception {
        MovieController controller = new MovieController(movieService);

        when(request.getServletPath()).thenReturn("/movie-detail");
        when(request.getParameter("id")).thenReturn("abc");
        when(request.getParameter("keyword")).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        controller.doGet(request, response);

        verify(response).sendRedirect("/movies?detailMessage=invalid_id");
        verifyNoInteractions(movieService);
    }

    @Test
    @DisplayName("UC04 - Movie null thì redirect về movie_not_available")
    void showMovieDetail_WhenMovieNull_ShouldRedirectMovieNotAvailable() throws Exception {
        MovieController controller = new MovieController(movieService);

        when(request.getServletPath()).thenReturn("/movie-detail");
        when(request.getParameter("id")).thenReturn("9999");
        when(request.getParameter("keyword")).thenReturn(null);
        when(request.getContextPath()).thenReturn("");
        when(movieService.getMovieDetail(9999)).thenReturn(null);

        controller.doGet(request, response);

        verify(movieService).getMovieDetail(9999);
        verify(response).sendRedirect("/movies?detailMessage=movie_not_available");
    }

    @Test
    @DisplayName("UC04 - Phim hợp lệ thì setAttribute và forward sang movie-detail.jsp")
    void showMovieDetail_WhenMovieExists_ShouldForwardToMovieDetailPage() throws Exception {
        MovieController controller = new MovieController(movieService);

        Movie movie = new Movie();
        movie.setId(1);
        movie.setTitle("Test Movie");

        when(request.getServletPath()).thenReturn("/movie-detail");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("keyword")).thenReturn("action");
        when(movieService.getMovieDetail(1)).thenReturn(movie);
        when(request.getRequestDispatcher("/movie-detail.jsp")).thenReturn(dispatcher);

        controller.doGet(request, response);

        verify(movieService).getMovieDetail(1);
        verify(request).setAttribute("movie", movie);
        verify(request).setAttribute("keyword", "action");
        verify(request).getRequestDispatcher("/movie-detail.jsp");
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }
}