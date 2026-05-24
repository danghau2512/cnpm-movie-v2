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

    public Showtime getShowtimeDetail(int showtimeId) {
        return showtimeDAO.findById(showtimeId);
    }

    public List<Seat> getSeatsByShowtime(int showtimeId) {
        return seatDAO.findSeatsByShowtimeId(showtimeId);
    }
    public int createBooking(int userId, int showtimeId, List<Integer> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất một ghế.");
        }
        return bookingDAO.createBooking(userId, showtimeId, seatIds);
    }
}