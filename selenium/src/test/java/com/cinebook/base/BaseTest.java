package com.cinebook.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.cinebook.utils.TestConfig;
import java.time.Duration;

/** Browser setup only. Team members write their own cases in tests/f01..f08. */
public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;
    protected TestConfig config;

    @BeforeEach
    void startBrowser() throws Exception {
        config = new TestConfig();
        baseUrl = config.get("baseUrl", "http://localhost:8080/CineBook_war_exploded").replaceAll("/+$", "");
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.fromString(config.get("pageLoadStrategy", "normal")));
        if (Boolean.parseBoolean(config.get("headless", "false")))
            options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,1000");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) driver.quit();
    }
}
