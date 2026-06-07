package Service;

import Dao.MovieDAO;
import Model.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieDAO movieDAO;

    @Test
    @DisplayName("UC04 - getMovieDetail trả về Movie khi phim tồn tại")
    void getMovieDetail_WhenMovieExists_ShouldReturnMovie() {
        Movie movie = new Movie();
        movie.setId(1);
        movie.setTitle("Test Movie");

        when(movieDAO.findById(1)).thenReturn(movie);

        MovieService movieService = new MovieService(movieDAO);

        Movie result = movieService.getMovieDetail(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Movie", result.getTitle());
        verify(movieDAO).findById(1);
    }

    @Test
    @DisplayName("UC04 - getMovieDetail trả về null khi phim không tồn tại hoặc không được hiển thị")
    void getMovieDetail_WhenMovieNotFound_ShouldReturnNull() {
        when(movieDAO.findById(9999)).thenReturn(null);

        MovieService movieService = new MovieService(movieDAO);

        Movie result = movieService.getMovieDetail(9999);

        assertNull(result);
        verify(movieDAO).findById(9999);
    }
}