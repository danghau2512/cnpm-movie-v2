package Controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

// UC05 - Development Testing: kiểm tra cấu hình Controller cho chức năng Xem lịch chiếu
class ShowtimeControllerTest {

    @Test
    @DisplayName("UC05 - Controller phải được mapping đúng đường dẫn /showtimes")
    void showtimeController_ShouldMapToShowtimesUrl() {
        WebServlet webServlet = ShowtimeController.class.getAnnotation(WebServlet.class);

        assertNotNull(webServlet, "ShowtimeController phải có annotation @WebServlet.");
        assertArrayEquals(new String[]{"/showtimes"}, webServlet.urlPatterns());
    }

    @Test
    @DisplayName("UC05 - Controller phải kế thừa HttpServlet")
    void showtimeController_ShouldExtendHttpServlet() {
        assertTrue(HttpServlet.class.isAssignableFrom(ShowtimeController.class));
    }

    @Test
    @DisplayName("UC05 - Controller phải có phương thức doGet để xử lý request xem lịch chiếu")
    void showtimeController_ShouldHaveDoGetMethod() throws NoSuchMethodException {
        Method doGetMethod = ShowtimeController.class.getDeclaredMethod(
                "doGet",
                jakarta.servlet.http.HttpServletRequest.class,
                jakarta.servlet.http.HttpServletResponse.class
        );

        assertNotNull(doGetMethod);
    }

    @Test
    @DisplayName("UC05 - Controller phải sử dụng ShowtimeService để lấy dữ liệu lịch chiếu")
    void showtimeController_ShouldContainShowtimeServiceField() throws NoSuchFieldException {
        Field field = ShowtimeController.class.getDeclaredField("showtimeService");

        assertEquals(Service.ShowtimeService.class, field.getType());
    }

    @Test
    @DisplayName("UC05 - Controller phải sử dụng MovieService để lấy danh sách phim và thể loại cho bộ lọc")
    void showtimeController_ShouldContainMovieServiceField() throws NoSuchFieldException {
        Field field = ShowtimeController.class.getDeclaredField("movieService");

        assertEquals(Service.MovieService.class, field.getType());
    }
}
