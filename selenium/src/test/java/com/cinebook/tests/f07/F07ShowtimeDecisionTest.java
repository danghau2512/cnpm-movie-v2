package com.cinebook.tests.f07;

import com.cinebook.base.TechniqueFeatureTest;
import com.cinebook.pages.AdminTechniquePage;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F07ShowtimeDecisionTest extends TechniqueFeatureTest {
    private MovieData movie;
    private LocalDate date;
    private AdminTechniquePage page;

    @BeforeEach
    void chuanBiPhim() {
        movie=prepare("Phim NOW_SHOWING dài 120 phút",this::createMovie);
        date=freeDate();
        page=new AdminTechniquePage(driver,baseUrl);
    }

    @Test @Order(1)
    @DisplayName("F07_Showtime_003 - R1: Đủ điều kiện mở lịch")
    void taoLichHopLe() {
        demonstrate("F07_Showtime_003","R1 - Phim đang chiếu, phòng hoạt động, giờ tương lai",()->
                luuLich(4,date,"09:00",true));
    }

    @Test @Order(2)
    @DisplayName("F07_Showtime_009 - R2: Phim HIDDEN")
    void khongMoLichPhimAn() {
        page.changeMovieStatus(movie.id(),"HIDDEN");
        demonstrate("F07_Showtime_009","R2 - Từ chối mở lịch phim HIDDEN",()-> {
            assertEquals("HIDDEN",db.value("SELECT status FROM movies WHERE id=?",movie.id()));
            luuLich(4,date,"09:00",false);
            assertTrue(page.error().contains("phim đang chiếu và phòng đang hoạt động"));
        });
    }

    @Test @Order(3)
    @DisplayName("F07_Showtime_010 - R3: Phòng INACTIVE")
    void khongMoLichPhongNgungHoatDong() {
        demonstrate("F07_Showtime_010","R3 - Từ chối phòng INACTIVE",()-> {
            assertEquals("INACTIVE",db.value("SELECT status FROM rooms WHERE id=90400"));
            luuLich(90400,date,"09:00",false);
            assertTrue(page.error().contains("phim đang chiếu và phòng đang hoạt động"));
        });
    }

    @Test @Order(4)
    @DisplayName("F07_Showtime_004 - R4: Giờ ở quá khứ")
    void khongMoLichQuaKhu() {
        demonstrate("F07_Showtime_004","R4 - Từ chối ngày đã qua",()-> {
            LocalDate yesterday=LocalDate.parse(db.value("SELECT DATE_FORMAT(DATE_SUB(CURDATE(),INTERVAL 1 DAY),'%Y-%m-%d')"));
            luuLich(4,yesterday,"09:00",false);
            assertTrue(page.error().contains("trong tương lai"));
        });
    }

    @Test @Order(5)
    @DisplayName("F07_Showtime_011 - R5: Trùng giờ cùng phòng")
    void tuChoiLichChongNhau() {
        luuLich(1,date,"18:00",true);
        demonstrate("F07_Showtime_011","R5 - 19:00 chồng suất 18:00–20:00",()-> {
            luuLich(1,date,"19:00",false);
            assertTrue(page.error().contains("trùng thời gian"));
        });
    }

    @Test @Order(6)
    @DisplayName("F07_Showtime_012 - R6: Cùng giờ, khác phòng")
    void choPhepCungGioKhacPhong() {
        luuLich(1,date,"18:00",true);
        demonstrate("F07_Showtime_012","R6 - Cùng 18:00 ở phòng 1 và phòng 4",()-> {
            luuLich(4,date,"18:00",true);
            assertEquals(2,db.number("SELECT COUNT(*) FROM showtimes WHERE movie_id=? AND start_time=? AND room_id IN (1,4)",movie.id(),date+" 18:00:00"));
        });
    }

    @Test @Order(7)
    @DisplayName("Thiết kế: 19:59 chồng 1 phút - chưa có mã TC")
    void batDauTruocMotPhut() { kiemTraBien("19:59",false,"DESIGN_F07_OVERLAP_MINUS_1"); }

    @Test @Order(8)
    @DisplayName("Thiết kế: 20:00 tiếp giáp - chưa có mã TC")
    void batDauDungLucKetThuc() { kiemTraBien("20:00",true,"DESIGN_F07_OVERLAP_EQUAL"); }

    @Test @Order(9)
    @DisplayName("Thiết kế: 20:01 cách 1 phút - chưa có mã TC")
    void batDauSauMotPhut() { kiemTraBien("20:01",true,"DESIGN_F07_OVERLAP_PLUS_1"); }

    private void kiemTraBien(String time,boolean valid,String tc) {
        luuLich(1,date,"18:00",true);
        demonstrate(tc,"Suất trước kết thúc 20:00, suất sau bắt đầu "+time,()->luuLich(1,date,time,valid));
    }

    private void luuLich(int room,LocalDate day,String time,boolean valid) {
        assertEquals(120,db.number("SELECT duration_minutes FROM movies WHERE id=?",movie.id()));
        int before=db.number("SELECT COUNT(*) FROM showtimes WHERE movie_id=?",movie.id());
        page.showtime(movie.id(),room,day,time,valid);
        assertEquals(before+(valid?1:0),db.number("SELECT COUNT(*) FROM showtimes WHERE movie_id=?",movie.id()));
        assertEquals(valid?1:0,db.number("SELECT COUNT(*) FROM showtimes WHERE movie_id=? AND room_id=? AND start_time=? AND price=90000 AND status='OPEN'",movie.id(),room,day+" "+time+":00"));
    }
}
