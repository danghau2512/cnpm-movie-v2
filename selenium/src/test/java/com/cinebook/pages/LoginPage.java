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
        wait.until(d -> !d.getCurrentUrl().contains("/login")
                || !d.findElements(By.cssSelector("#loginMessage")).isEmpty()
                && !d.findElement(By.id("loginMessage")).getText().isBlank());
        assertEquals(baseUrl + (admin ? "/admin/dashboard" : "/home"), driver.getCurrentUrl(),
                "Login/role redirect failed. Check ACTIVE account and role. " + text());
    }

    public void logout() {
        open("/logout");
        open("/booking-history");
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/login"));
        assertTrue(visible(By.id("loginForm")).isDisplayed());
    }
}
