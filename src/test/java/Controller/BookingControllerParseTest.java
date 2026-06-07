package Controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
// issue 12
class BookingControllerParseTest {

    private Integer callParsePositiveInt(String value) throws Exception {
        BookingController controller = new BookingController();

        Method method = BookingController.class.getDeclaredMethod("parsePositiveInt", String.class);
        method.setAccessible(true);

        return (Integer) method.invoke(controller, value);
    }

    @Test
    @DisplayName("Issue #12 - showtimeId hợp lệ phải parse thành số nguyên dương")
    void parsePositiveInt_WithValidNumber_ShouldReturnInteger() throws Exception {
        Integer result = callParsePositiveInt("1");

        assertEquals(1, result);
    }

    @Test
    @DisplayName("Issue #12 - showtimeId null phải trả về null")
    void parsePositiveInt_WithNull_ShouldReturnNull() throws Exception {
        Integer result = callParsePositiveInt(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Issue #12 - showtimeId rỗng phải trả về null")
    void parsePositiveInt_WithEmptyValue_ShouldReturnNull() throws Exception {
        Integer result = callParsePositiveInt("");

        assertNull(result);
    }

    @Test
    @DisplayName("Issue #12 - showtimeId sai định dạng phải trả về null")
    void parsePositiveInt_WithInvalidText_ShouldReturnNull() throws Exception {
        Integer result = callParsePositiveInt("abc");

        assertNull(result);
    }

    @Test
    @DisplayName("Issue #12 - showtimeId bằng 0 phải trả về null")
    void parsePositiveInt_WithZero_ShouldReturnNull() throws Exception {
        Integer result = callParsePositiveInt("0");

        assertNull(result);
    }

    @Test
    @DisplayName("Issue #12 - showtimeId âm phải trả về null")
    void parsePositiveInt_WithNegativeNumber_ShouldReturnNull() throws Exception {
        Integer result = callParsePositiveInt("-1");

        assertNull(result);
    }
}