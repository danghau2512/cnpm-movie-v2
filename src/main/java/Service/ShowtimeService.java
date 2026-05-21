package Service;

import Dao.ShowtimeDAO;
import Model.Showtime;

import java.util.List;

public class ShowtimeService {
    private final ShowtimeDAO showtimeDAO = new ShowtimeDAO();

    // UC05 - 4.7.5: Kiểm tra movieId để xác định lấy tất cả lịch chiếu hay lấy theo phim
    public List<Showtime> getShowtimes(String movieIdRaw) {

        // UC05 - 4.8.1: Trường hợp không có movieId hoặc movieId rỗng
        // UC05 - 4.7.6: Gọi DAO lấy tất cả suất chiếu đang mở
        if (movieIdRaw == null || movieIdRaw.trim().isEmpty()) {
            return showtimeDAO.findAllOpen();
        }

        // UC05 - 4.8.5: Trường hợp có movieId từ request
        int movieId = Integer.parseInt(movieIdRaw);

        // UC05 - 4.7.7: Gọi DAO lấy lịch chiếu theo phim được chọn
        return showtimeDAO.findByMovieId(movieId);
    }
}