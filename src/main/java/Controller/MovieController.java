package Controller;

import Model.Movie;
import Service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/movies", "/movie-detail"})
public class MovieController extends HttpServlet {
    private final MovieService movieService = new MovieService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();

        if ("/movies".equals(path)) {
            showMovieList(request, response);
        } else if ("/movie-detail".equals(path)) {
            showMovieDetail(request, response);
        }
    }

    private void showMovieList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");

        List<Movie> movies = movieService.getMovies(keyword);

        request.setAttribute("movies", movies);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher("/movies.jsp")
                .forward(request, response);
    }

    private void showMovieDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idRaw = request.getParameter("id");

        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/movies");
            return;
        }

        int id = Integer.parseInt(idRaw);

        Movie movie = movieService.getMovieDetail(id);

        if (movie == null) {
            response.sendRedirect(request.getContextPath() + "/movies");
            return;
        }

        request.setAttribute("movie", movie);

        request.getRequestDispatcher("/movie-detail.jsp")
                .forward(request, response);
    }
}