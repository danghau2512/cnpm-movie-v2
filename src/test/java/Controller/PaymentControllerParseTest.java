package Controller;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentControllerParseTest {

    @Test
    void parseBookingId_WithValidNumber_ShouldReturnInteger() throws Exception {
        Integer result = callParseBookingId("12");

        assertEquals(12, result);
    }

    @Test
    void parseBookingId_WithNumberHasSpace_ShouldReturnInteger() throws Exception {
        Integer result = callParseBookingId(" 12 ");

        assertEquals(12, result);
    }

    @Test
    void parseBookingId_WithNullValue_ShouldReturnNull() throws Exception {
        Integer result = callParseBookingId(null);

        assertNull(result);
    }

    @Test
    void parseBookingId_WithEmptyValue_ShouldReturnNull() throws Exception {
        Integer result = callParseBookingId("");

        assertNull(result);
    }

    @Test
    void parseBookingId_WithBlankValue_ShouldReturnNull() throws Exception {
        Integer result = callParseBookingId("   ");

        assertNull(result);
    }

    @Test
    void parseBookingId_WithInvalidText_ShouldReturnNull() throws Exception {
        Integer result = callParseBookingId("abc");

        assertNull(result);
    }

    @Test
    void parseBookingId_WithZero_ShouldReturnNull() throws Exception {
        Integer result = callParseBookingId("0");

        assertNull(result);
    }

    @Test
    void parseBookingId_WithNegativeNumber_ShouldReturnNull() throws Exception {
        Integer result = callParseBookingId("-1");

        assertNull(result);
    }

    private Integer callParseBookingId(String value) throws Exception {
        PaymentController controller = new PaymentController();

        Method method = PaymentController.class.getDeclaredMethod(
                "parseBookingId",
                String.class
        );

        method.setAccessible(true);

        return (Integer) method.invoke(controller, value);
    }
}