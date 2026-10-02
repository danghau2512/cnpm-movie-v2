package Service;

import Dao.BookingManagementDAO;
import Model.BookingRecord;
import java.util.List;

public class BookingManagementService {
    private final BookingManagementDAO dao;
    public BookingManagementService() { this(new BookingManagementDAO()); }
    public BookingManagementService(BookingManagementDAO dao) { this.dao = dao; }
    public List<BookingRecord> getHistory(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("Tài khoản không hợp lệ.");
        return dao.findBookings(userId, "", "");
    }
    public List<BookingRecord> getBookings(String status, String keyword) {
        status = status == null ? "" : status.trim();
        keyword = keyword == null ? "" : keyword.trim();
        if (!List.of("", "PENDING", "CONFIRMED", "CANCELLED").contains(status))
            throw new IllegalArgumentException("Trạng thái đơn không hợp lệ.");
        if (keyword.length() > 150) throw new IllegalArgumentException("Từ khóa quá dài.");
        return dao.findBookings(null, status, keyword);
    }
    public void updateBooking(int id, String action) {
        if (id <= 0 || !("confirm".equals(action) || "cancel".equals(action)))
            throw new IllegalArgumentException("Thao tác đơn đặt vé không hợp lệ.");
        dao.updateBooking(id, action);
    }
}
