package com.cinebook.tests.f01;

import com.cinebook.base.TechniqueFeatureTest;
import com.cinebook.pages.LoginTechniquePage;
import org.junit.jupiter.api.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class F01EquivalencePartitionTest extends TechniqueFeatureTest {
    @Test @Order(1)
    @DisplayName("F01_Login_008 - Tài khoản ACTIVE, thông tin đúng")
    void dangNhapDungThongTin() {
        demonstrate("F01_Login_008", "Đăng nhập hợp lệ", () -> {
            assertEquals("CUSTOMER/ACTIVE", db.value("SELECT CONCAT(role,'/',status) FROM users WHERE email=?", config.account("customer").email()));
            login.login(config.account("customer"), false);
            driver.get(baseUrl + "/booking-history");
            assertEquals(baseUrl + "/booking-history", driver.getCurrentUrl());
        });
    }

    @Test @Order(2)
    @DisplayName("F01_Login_009 - Email chưa đăng ký")
    void emailChuaDangKy() {
        String email = "chua-co-" + UUID.randomUUID().toString().replace("-", "") + "@example.test";
        demonstrate("F01_Login_009", "Email đúng định dạng nhưng chưa có tài khoản", () -> {
            assertEquals(0, db.number("SELECT COUNT(*) FROM users WHERE email=?", email));
            kiemTraTuChoi(email, "TestDesign_2026!");
        });
    }

    @Test @Order(3)
    @DisplayName("F01_Login_001 - Mật khẩu sai")
    void matKhauSai() {
        demonstrate("F01_Login_001", "Tài khoản có thật, mật khẩu sai", () ->
                kiemTraTuChoi(config.account("customer").email(), config.account("customer").password() + "_sai"));
    }

    @Test @Order(4)
    @DisplayName("F01_Login_005 - Tài khoản bị khóa")
    void taiKhoanKhongActive() {
        demonstrate("F01_Login_005", "Mật khẩu đúng nhưng tài khoản LOCKED", () -> {
            assertEquals("LOCKED", db.value("SELECT status FROM users WHERE email='design.locked@example.test'"));
            assertEquals("TestDesign_2026!", db.value("SELECT password_hash FROM users WHERE email='design.locked@example.test'"));
            kiemTraTuChoi("design.locked@example.test", "TestDesign_2026!");
        });
    }

    private void kiemTraTuChoi(String email, String password) {
        LoginTechniquePage page = new LoginTechniquePage(driver, baseUrl);
        page.rejected(email, password);
        page.assertNoSession();
    }
}
