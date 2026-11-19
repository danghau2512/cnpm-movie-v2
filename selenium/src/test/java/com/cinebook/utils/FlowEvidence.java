package com.cinebook.utils;

import org.openqa.selenium.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** Browser evidence only; never logs credentials or makes assertions pass. */
public final class FlowEvidence {
    private final WebDriver driver;
    private final Path directory;
    private final boolean screenshots;
    private int number;

    public FlowEvidence(WebDriver driver, String testName, boolean screenshots) {
        this.driver = driver;
        this.screenshots = screenshots;
        String run = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
                + "-" + UUID.randomUUID().toString().substring(0, 8);
        directory = Path.of("target", "evidence", run + "-" + testName);
        try { Files.createDirectories(directory); }
        catch (IOException e) { throw new UncheckedIOException(e); }
        System.out.println("Evidence: " + directory.toAbsolutePath());
    }

    public void step(String id, String description, Runnable action) {
        String stem = String.format("%02d-%s", ++number, id);
        try {
            action.run();
            record(stem, "PASS", description);
        } catch (RuntimeException | AssertionError error) {
            record(stem, "FAIL", description);
            throw error;
        }
    }

    private void record(String stem, String status, String description) {
        String message = stem + " " + status + " " + description;
        System.out.println(message);
        try {
            Files.writeString(directory.resolve("steps.log"), message + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (screenshots) Files.write(directory.resolve(stem + "-" + status + ".png"),
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        } catch (IOException | WebDriverException e) {
            System.err.println("Could not save browser evidence: " + e.getClass().getSimpleName());
        }
    }
}
