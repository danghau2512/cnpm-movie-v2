package Service;

import Dao.BookingDAO;
import Dao.SeatDAO;
import Dao.ShowtimeDAO;
import Model.Seat;
import Model.Showtime;

import java.util.List;

public class BookingService {
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    // UC06 - 6.1.2: Lấy thông tin chi tiết suất chiếu.
    public Showtime getShowtimeDetail(int showtimeId) {
        // UC06 - 6.1.3: Gọi ShowtimeDAO truy vấn thông tin suất chiếu trong database
        return showtimeDAO.findById(showtimeId);
    }
    // UC06 - 6.1.4: Lấy danh sách ghế theo suất chiếu.

    public List<Seat> getSeatsByShowtime(int showtimeId) {
        // UC06 - 6.1.5: Gọi SeatDAO truy vấn danh sách ghế và trạng thái ghế
        return seatDAO.findSeatsByShowtimeId(showtimeId);
    }
    // UC06 - 6.1.9: Kiểm tra dữ liệu đặt vé trước khi tạo đơn.
    public int createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        // UC06 - 6.2.3: Kiểm tra trường hợp khách hàng chưa chọn ghế
        if (seatIds == null || seatIds.isEmpty()) {
            // UC06 - 6.2.4: Trả về thông báo lỗi yêu cầu chọn ít nhất một ghế
            throw new RuntimeException("Vui lòng chọn ít nhất một ghế.");
        }
        // UC06 - 6.1.10: Gọi BookingDAO xử lý tạo đơn đặt vé trong database
        return bookingDAO.createBooking(userId, showtimeId, seatIds);
    }
}