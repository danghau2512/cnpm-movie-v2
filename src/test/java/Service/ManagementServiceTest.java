package Service;

import Dao.AdminDAO;
import Dao.BookingManagementDAO;
import Model.Movie;
import Model.Showtime;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagementServiceTest {
    @Test void historyAlwaysPassesAuthenticatedUserIdToDao() {
        BookingManagementDAO dao = mock(BookingManagementDAO.class); BookingManagementService service = new BookingManagementService(dao);
        service.getHistory(7); verify(dao).findBookings(7, "", "");
        assertThrows(IllegalArgumentException.class, () -> service.getHistory(0));
        assertThrows(IllegalArgumentException.class, () -> service.updateBooking(1, "paid"));
    }
    @Test void invalidMovieAndShowtimeNeverReachPersistence() {
        AdminDAO dao = mock(AdminDAO.class); AdminService service = new AdminService(dao);
        Movie m = new Movie(); m.setTitle("A"); m.setAgeRating("P"); m.setDurationMinutes(120); m.setStatus("NOW_SHOWING");
        m.setPosterUrl("javascript:alert(1)");
        assertThrows(IllegalArgumentException.class, () -> service.saveMovie(m, List.of()));
        Showtime s = new Showtime(); s.setMovieId(1); s.setRoomId(1); s.setPrice(90000); s.setStatus("OPEN"); s.setShowDate("2026-02-30"); s.setShowTime("19:00");
        assertThrows(IllegalArgumentException.class, () -> service.saveShowtime(s));
        verifyNoInteractions(dao);
    }
}
