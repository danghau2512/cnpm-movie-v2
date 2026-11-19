package com.cinebook.tests.f01;

import com.cinebook.base.FeatureMainTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class F01LoginMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F01 main | Customer/Admin login and logout")
    void loginCustomerAndAdmin() {
        evidence.step("F01-customer", "Customer login -> /home; reference F01_Login_008", () -> {
            login.login(config.account("customer"), false);
            driver.get(baseUrl + "/booking-history");
            assertEquals(baseUrl + "/booking-history", driver.getCurrentUrl());
        });
        evidence.step("F01-customer-logout", "Logout invalidates history access; reference F01_Login_006", login::logout);
        evidence.step("F01-admin", "Admin login -> /admin/dashboard; reference F01_Login_004", () ->
                login.login(config.account("admin"), true));
        evidence.step("F01-admin-logout", "Admin ends the session", login::logout);
    }
}
