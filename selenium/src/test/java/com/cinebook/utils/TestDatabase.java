package com.cinebook.utils;

import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

/** Chỉ SELECT để đối chiếu kết quả Selenium, không tạo hay sửa dữ liệu. */
public class TestDatabase implements AutoCloseable {
    private final Connection connection;

    public TestDatabase(TestConfig config) throws SQLException {
        connection = DriverManager.getConnection(config.required("technique.dbUrl"),
                config.get("technique.dbUser", "root"), config.get("technique.dbPassword", ""));
        connection.setReadOnly(true);
        assertTrue(value("SELECT DATABASE()").startsWith("cinebook_"), "Hãy dùng DB thử cinebook_*, cùng DB của web đang chạy");
    }

    public String value(String sql, Object... values) {
        if (!sql.stripLeading().toUpperCase(java.util.Locale.ROOT).startsWith("SELECT "))
            throw new IllegalArgumentException("Chỉ cho phép SELECT");
        try (PreparedStatement query = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) query.setObject(i + 1, values[i]);
            try (ResultSet result = query.executeQuery()) {
                return result.next() ? result.getString(1) : null;
            }
        } catch (SQLException e) { throw new IllegalStateException("Không đọc được DB thử", e); }
    }

    public int number(String sql, Object... values) { return Integer.parseInt(value(sql, values)); }

    public void bookingState(int id, String booking, String payment) {
        assertEquals(booking + "/" + payment,
                value("SELECT CONCAT(booking_status,'/',payment_status) FROM bookings WHERE id=?", id));
    }

    public String paymentSnapshot(int id) {
        return value("SELECT GROUP_CONCAT(CONCAT(id,':',payment_method,':',amount,':',payment_status,':',IFNULL(transaction_code,''),':',IFNULL(paid_at,'')) ORDER BY id) FROM payments WHERE booking_id=?", id);
    }

    @Override public void close() throws SQLException { connection.close(); }
}
