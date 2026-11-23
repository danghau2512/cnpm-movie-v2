package com.cinebook.tests.f03;

import com.cinebook.base.TechniqueTest;
import com.cinebook.pages.*;
import com.cinebook.pages.AdminScheduleStatePage.Snapshot;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F03DecisionTableTest extends TechniqueTest {
    private WebDriver adminDriver;
    private AdminScheduleStatePage admin;
    private ScheduleTechniquePage schedules;
    private LocalDate date;

    @BeforeEach
    void chuanBiDuLieu() {
        // Admin dùng phiên riêng để đổi trạng thái mà khách không bị đăng xuất.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000");
        adminDriver = new ChromeDriver(options);
        adminDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        new LoginPage(adminDriver, baseUrl).login(config.account("admin"), true);
        admin = new AdminScheduleStatePage(adminDriver, baseUrl);
        schedules = new ScheduleTechniquePage(driver, baseUrl);
        date = admin.read(90301).date();
        assertTrue(date.isAfter(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))), "Cần chạy lại seed để lịch nằm trong tương lai");
        new LoginPage(driver, baseUrl).login(config.account("customer"), false);
    }

    @AfterEach
    void dongTrinhDuyetAdmin() {
        if (adminDriver != null) adminDriver.quit();
    }

    @Test
    @Order(1)
    @DisplayName("F03_Schedule_005 - R1: OPEN, tương lai, khớp bộ lọc")
    void hienLichKhiDuDieuKien() {
        demonstrate("F03_Schedule_005", "R1 - Hiển thị hai suất phù hợp", () -> {
            kiemTraDuLieu(90301, 90301, "OPEN", date);
            kiemTraDuLieu(90302, 90301, "OPEN", date);
            schedules.filter(90301, date, "Horror");
            schedules.assertIds(90301, 90302);
            schedules.assertCard(90301, date, "09:00");
            schedules.assertCard(90302, date, "14:00");
        });
    }

    @Test
    @Order(2)
    @DisplayName("F03_Schedule_006 - R2: Không khớp thể loại")
    void khongHienLichKhiSaiTheLoai() {
        demonstrate("F03_Schedule_006", "R2 - Phim Horror không khớp bộ lọc Action", () -> {
            kiemTraDuLieu(90301, 90301, "OPEN", date);
            schedules.filter(90301, date, "Horror");
            schedules.assertIds(90301, 90302);
            schedules.filter(90301, date, "Action");
            schedules.assertIds();
        });
    }

    @Test
    @Order(3)
    @DisplayName("F03_Schedule_011 - R3: OPEN nhưng đã bắt đầu")
    void khongHienSuatDaBatDau() {
        demonstrate("F03_Schedule_011", "R3 - Loại suất OPEN ở quá khứ", () -> {
            kiemTraDuLieu(90304, 90303, "OPEN", date.minusDays(8));
            assertTrue(admin.read(90304).date().isBefore(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))));
            kiemTraDuLieu(90305, 90303, "CLOSED", date);
            kiemTraDuLieu(90306, 90303, "CANCELLED", date);
            schedules.filter(90303, null, "");
            schedules.assertIds();
        });
    }

    @Test
    @Order(4)
    @DisplayName("F03_Schedule_012 - R4: Suất CLOSED")
    void khongHienSuatDaDong() {
        kiemTraDoiTrangThai("CLOSED", 90305, "F03_Schedule_012");
    }

    @Test
    @Order(5)
    @DisplayName("F03_Schedule_013 - R5: Suất CANCELLED")
    void khongHienSuatDaHuy() {
        kiemTraDoiTrangThai("CANCELLED", 90306, "F03_Schedule_013");
    }

    private void kiemTraDuLieu(int id, int movie, String status, LocalDate day) {
        Snapshot row = admin.read(id);
        assertEquals(movie, row.movie());
        assertEquals(1, row.room());
        assertEquals(status, row.status());
        assertEquals(day, row.date());
        assertEquals(80000, row.price());
    }

    private void kiemTraDoiTrangThai(String status, int fixedShow, String tc) {
        kiemTraDuLieu(fixedShow, 90303, status, date);
        kiemTraDuLieu(90307, 90301, "OPEN", date.plusDays(2));
        schedules.filter(90303, null, "");
        schedules.assertIds();
        schedules.filter(90301, null, "");
        schedules.assertIds(90301, 90302, 90303, 90307);
        String oldUrl = schedules.bookingUrl(90307);
        evidence.step(tc + "-BEFORE", "Suất 90307 đang OPEN", () -> schedules.assertIds(90301, 90302, 90303, 90307));

        try {
            admin.setStatus(90307, status);
            demonstrate(tc, "Suất " + status + " không còn trong danh sách", () -> {
                schedules.filter(90301, null, "");
                schedules.assertIds(90301, 90302, 90303);
                schedules.assertStaleUrlRejected(oldUrl);
                schedules.filter(90301, null, "");
                schedules.assertIds(90301, 90302, 90303);
            });
        } finally {
            // Khôi phục suất thử kể cả khi assertion không đạt.
            admin.setStatus(90307, "OPEN");
            schedules.filter(90301, null, "");
            evidence.step(tc + "-RESTORED", "Đã trả suất 90307 về OPEN", () -> schedules.assertIds(90301, 90302, 90303, 90307));
        }
    }
}
