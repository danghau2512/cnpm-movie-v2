package Service;

import Dao.ShowtimeDAO;
import Model.Showtime;

import java.util.List;

public class ShowtimeService {
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();

    // UC05 - 4.7.5: Kiểm tra movieId để xác định lấy tất cả lịch chiếu hay lấy theo phim
    public List<Showtime> getShowtimes(String movieIdRaw, String showDate) {

        if (showDate != null && showDate.trim().isEmpty()) {
            showDate = null;
        }

        // UC05 - 4.8.1: Trường hợp không có movieId hoặc movieId rỗng
        if (movieIdRaw == null || movieIdRaw.trim().isEmpty()) {
            return showtimeDAO.findAllOpen(showDate);
        }

        try {
            int movieId = Integer.parseInt(movieIdRaw.trim());

            // Trường hợp movieId không hợp lệ như 0 hoặc số âm thì hiển thị tất cả lịch chiếu
            if (movieId <= 0) {
                return showtimeDAO.findAllOpen(showDate);
            }

            // UC05 - 4.7.7: Gọi DAO lấy lịch chiếu theo phim được chọn
            return showtimeDAO.findByMovieId(movieId, showDate);

        } catch (NumberFormatException e) {
            // Tránh lỗi khi người dùng sửa URL thành /showtimes?movieId=abc
            return showtimeDAO.findAllOpen(showDate);
        }
    }
}