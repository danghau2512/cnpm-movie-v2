package com.cinebook.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTechniquePage extends UiPage {
    public LoginTechniquePage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public void rejected(String email, String password) {
        open("/login");
        fill("email", email); fill("password", password);
        WebElement button = visible(By.cssSelector("#loginForm button[type=submit]"));
        click(button); awaitDetached(button);
        assertEquals(baseUrl + "/login", driver.getCurrentUrl());
        assertEquals("Email hoặc mật khẩu không đúng.", visible(By.id("loginMessage")).getText());
    }

    public void assertNoSession() {
        String loginTab = driver.getWindowHandle();
        driver.switchTo().newWindow(WindowType.TAB);
        try {
            open("/booking-history");
            wait.until(ExpectedConditions.urlToBe(baseUrl + "/login"));
            assertTrue(visible(By.id("loginForm")).isDisplayed());
        } finally {
            driver.close();
            driver.switchTo().window(loginTab);
        }
    }
}
