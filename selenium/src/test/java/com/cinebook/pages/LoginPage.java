package com.cinebook.pages;

import com.cinebook.utils.TestConfig.Account;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public final class LoginPage extends UiPage {
    public LoginPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void login(Account account, boolean admin) {
        open("/login");
        fill("email", account.email());
        fill("password", account.password());
        click(By.cssSelector("#loginForm button[type=submit]"));
        String destination = baseUrl + (admin ? "/admin/dashboard" : "/home");
        wait.withMessage("Expected login to redirect to " + destination)
                .until(ExpectedConditions.urlToBe(destination));
    }

    public void logout() {
        open("/logout");
        open("/booking-history");
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/login"));
        assertTrue(visible(By.id("loginForm")).isDisplayed());
    }
}
