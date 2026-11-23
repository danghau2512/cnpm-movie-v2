package com.cinebook.tests.f06;

import com.cinebook.base.TechniqueFeatureTest;
import com.cinebook.pages.AdminTechniquePage;
import org.junit.jupiter.api.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F06DurationBoundaryTest extends TechniqueFeatureTest {
    @BeforeEach void dangNhapAdmin() { loginAs("admin"); }

    @Test @Order(1) @DisplayName("F06_Movie_006 - Thời lượng 0 phút")
    void thoiLuongBangKhong() { kiemTraThoiLuong(0,"F06_Movie_006",false); }
    @Test @Order(2) @DisplayName("F06_Movie_007 - Thời lượng 1 phút")
    void thoiLuongBangMot() { kiemTraThoiLuong(1,"F06_Movie_007",true); }
    @Test @Order(3) @DisplayName("Thiết kế: thời lượng 2 phút - chưa có mã TC")
    void thoiLuongBangHai() { kiemTraThoiLuong(2,"DESIGN_F06_DURATION_2",true); }
    @Test @Order(4) @DisplayName("Thiết kế: thời lượng 599 phút - chưa có mã TC")
    void thoiLuongBang599() { kiemTraThoiLuong(599,"DESIGN_F06_DURATION_599",true); }
    @Test @Order(5) @DisplayName("F06_Movie_008 - Thời lượng 600 phút")
    void thoiLuongBang600() { kiemTraThoiLuong(600,"F06_Movie_008",true); }
    @Test @Order(6) @DisplayName("F06_Movie_009 - Thời lượng 601 phút")
    void thoiLuongBang601() { kiemTraThoiLuong(601,"F06_Movie_009",false); }

    private void kiemTraThoiLuong(int duration,String tc,boolean valid) {
        String title="F06Demo TL"+duration+" "+UUID.randomUUID().toString().substring(0,8);
        demonstrate(tc,"Biên thời lượng "+duration+" phút",()-> {
            AdminTechniquePage page=new AdminTechniquePage(driver,baseUrl);
            page.movieForm(title,duration);
            page.saveMovie(valid,duration);
            assertEquals(valid?1:0,db.number("SELECT COUNT(*) FROM movies WHERE title=?",title));
            if(valid) {
                int id=db.number("SELECT id FROM movies WHERE title=?",title);
                assertEquals(duration,db.number("SELECT duration_minutes FROM movies WHERE id=?",id));
                page.assertMovieReadback(id,title,duration);
            }
        });
    }
}
