package com.cinebook.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public abstract class UiPage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final String baseUrl;

    protected UiPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected void open(String path) {
        driver.get(baseUrl + path);
        assertFalse(text().contains("HTTP Status 500"), "Web returned HTTP 500: " + path);
        assertFalse(text().contains("HTTP Status 404"), "Web returned HTTP 404: " + path);
    }

    protected String text() { return driver.findElement(By.tagName("body")).getText(); }
    protected WebElement visible(By selector) { return wait.until(ExpectedConditions.visibilityOfElementLocated(selector)); }
    protected void fill(String name, String value) {
        WebElement input = visible(By.name(name));
        input.clear();
        input.sendKeys(value);
    }
    protected void select(String name, String value) { new Select(visible(By.name(name))).selectByValue(value); }
    protected void click(By selector) { click(wait.until(ExpectedConditions.elementToBeClickable(selector))); }
    protected void click(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
        element.click();
    }
    protected void dateOrTime(String name, String value) {
        // Browser date/time widgets depend on locale; set their DOM values, then submit normally.
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value=arguments[1];arguments[0].dispatchEvent(new Event('input',{bubbles:true}));"
                + "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", visible(By.name(name)), value);
    }
    protected void success() {
        WebElement notice = visible(By.cssSelector(".notice"));
        assertTrue(notice.getDomAttribute("class").contains("success"), notice.getText());
        assertEquals("Thao tác thành công.", notice.getText());
    }
    protected void awaitDetached(WebElement previousPageElement) {
        wait.until(d -> {
            try {
                previousPageElement.isEnabled();
                return false;
            } catch (StaleElementReferenceException e) {
                return true;
            } catch (WebDriverException e) {
                // Chrome 157 sometimes reports a detached document node as a generic inspector error.
                if (e.getMessage() != null && e.getMessage().contains("Node with given id does not belong to the document"))
                    return true;
                throw e;
            }
        });
    }
    protected static String xpathText(String text) {
        if (!text.contains("'")) return "'" + text + "'";
        if (!text.contains("\"")) return "\"" + text + "\"";
        return "concat('" + text.replace("'", "',\"'\",'") + "')";
    }
}
