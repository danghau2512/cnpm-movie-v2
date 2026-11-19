package com.cinebook.tests.f06;

import com.cinebook.base.FeatureMainTest;
import com.cinebook.pages.AdminMoviesPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;

public class F06MovieMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F06 main | Admin adds movie and reads back its fields")
    void addMovie() {
        prepare("Log in as Admin", () -> { loginAs("admin"); return true; });
        evidence.step("F06-create", "Create NOW_SHOWING movie with Action genre; reference F06_Movie_003", () ->
                new AdminMoviesPage(driver, baseUrl).create("SEL_MAIN_" + UUID.randomUUID().toString().replace("-", "")));
    }
}
