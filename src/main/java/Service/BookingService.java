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

    /**
     * UC06 - Đặt vé
     * Lấy và kiểm tra thông tin suất chiếu trước khi hiển thị trang chọn ghế.
     */
    public Showtime getShowtimeDetail(int showtimeId) {
        // UC06 - 6.1.5: BookingService gọi ShowtimeDAO.findById(showtimeId)
        Showtime showtime = showtimeDAO.findById(showtimeId);

        // UC06 - 6.1.6: BookingService kiểm tra suất chiếu có tồn tại và còn cho phép đặt vé hay không
        if (showtime == null || !"OPEN".equalsIgnoreCase(showtime.getStatus())) {
            // UC06 - 6.1.6A.1: Suất chiếu không tồn tại hoặc không còn cho phép đặt vé
            return null;
        }

        return showtime;
    }

    /**
     * UC06 - Đặt vé
     * Lấy danh sách ghế và trạng thái ghế theo suất chiếu.
     */
    public List<Seat> getSeatsByShowtime(int showtimeId) {
        // UC06 - 6.1.8: BookingService gọi SeatDAO.findSeatsByShowtimeId(showtimeId)
        List<Seat> seats = seatDAO.findSeatsByShowtimeId(showtimeId);

        // UC06 - 6.1.9: SeatDAO trả về danh sách ghế và trạng thái ghế cho BookingService
        return seats;
    }

    /**
     * UC06 - Đặt vé
     * Kiểm tra dữ liệu đặt vé và gọi DAO để tạo đơn đặt vé.
     */
    public int createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        // UC06 - 6.1.16: BookingService kiểm tra danh sách ghế được chọn có rỗng hoặc không hợp lệ hay không
        if (seatIds == null || seatIds.isEmpty()) {
            // UC06 - 6.1.16A.1 - 6.1.16A.2: Khách hàng chưa chọn ghế, trả lỗi về BookingController
            throw new RuntimeException("Vui lòng chọn ít nhất một ghế.");
        }

        // UC06 - 6.1.17: BookingService gọi BookingDAO.createBooking(userId, showtimeId, seatIds)
        return bookingDAO.createBooking(userId, showtimeId, seatIds);
    }
}