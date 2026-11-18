package com.cinebook.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/** Browser setup only. Team members write their own cases in tests/f01..f08. */
public abstract class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeEach
    void startBrowser() throws Exception {
        Properties config = new Properties();
        try (InputStream stream = getClass().getResourceAsStream("/selenium.properties")) {
            if (stream != null) config.load(stream);
        }
        baseUrl = System.getProperty("cinebook.baseUrl", config.getProperty("baseUrl",
                "http://localhost:8080/CineBook_war_exploded")).replaceAll("/+$", "");
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("cinebook.headless", config.getProperty("headless", "false"))))
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
