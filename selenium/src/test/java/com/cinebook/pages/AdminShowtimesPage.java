package com.cinebook.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.*;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

public final class AdminShowtimesPage extends UiPage {
    public record Show(int id, int movieId, String title, LocalDate date, String time, String room, long price) {}
    public AdminShowtimesPage(WebDriver driver, String baseUrl) { super(driver, baseUrl); }

    public Show create(int movieId, String title, String roomId, int daysAhead) {
        open("/admin/showtimes");
        select("movieId", "" + movieId);
        select("roomId", roomId);
        String roomOption = new org.openqa.selenium.support.ui.Select(visible(By.name("roomId"))).getFirstSelectedOption().getText();
        assertTrue(roomOption.contains("Hoạt động"), "Select an ACTIVE room in flow.roomId");
        String room = roomOption.substring(0, roomOption.lastIndexOf(" (")).trim();
        // Read all existing schedules and choose a free two-hour slot in this room.
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        java.util.List<LocalDateTime> occupied = new java.util.ArrayList<>();
        for (WebElement row : driver.findElements(By.cssSelector("tbody tr"))) {
            var cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 6 && cells.get(1).getText().equals(room))
                occupied.add(LocalDateTime.parse(cells.get(2).getText(), format));
        }
        // Avoid each occupied day entirely: existing movies can have differing durations.
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusDays(daysAhead);
        for (int i = 0; i < 366; i++, date = date.plusDays(1)) {
            LocalDate candidate = date;
            if (occupied.stream().noneMatch(t -> !t.toLocalDate().isBefore(candidate.minusDays(1))
                    && !t.toLocalDate().isAfter(candidate.plusDays(1)))) break;
            assertTrue(i < 365, "No free day found for selected room");
        }
        dateOrTime("showDate", date.toString());
        dateOrTime("showTime", "09:00");
        fill("price", "80000");
        select("status", "OPEN");
        WebElement submit = visible(By.cssSelector(".management-form button"));
        click(submit);
        awaitDetached(submit);
        success();
        WebElement row = visible(By.xpath("//tbody/tr[td[1][normalize-space(.)=" + xpathText(title) + "]]"));
        assertTrue(row.getText().contains(room));
        assertTrue(row.getText().contains(date + " 09:00"));
        assertTrue(row.getText().contains("80000"));
        assertTrue(row.getText().contains("Mở đặt vé"));
        String href = row.findElement(By.cssSelector("a[href*='?edit=']")).getDomAttribute("href");
        int id = Integer.parseInt(href.substring(href.lastIndexOf('=') + 1));
        open("/admin/showtimes?edit=" + id);
        assertEquals("" + movieId, visible(By.name("movieId")).getDomProperty("value"));
        assertEquals(roomId, visible(By.name("roomId")).getDomProperty("value"));
        assertEquals(date.toString(), visible(By.name("showDate")).getDomProperty("value"));
        assertEquals("80000", visible(By.name("price")).getDomProperty("value"));
        assertEquals("OPEN", visible(By.name("status")).getDomProperty("value"));
        return new Show(id, movieId, title, date, "09:00", room, 80000);
    }
}
