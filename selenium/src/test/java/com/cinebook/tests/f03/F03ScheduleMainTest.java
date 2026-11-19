package com.cinebook.tests.f03;

import com.cinebook.base.FeatureMainTest;
import com.cinebook.pages.ShowtimesPage;
import com.cinebook.pages.AdminShowtimesPage.Show;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class F03ScheduleMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F03 main | Filter schedule and open correct seat map")
    void filterScheduleAndOpenBooking() {
        Show show = prepare("Create a future show and log in as Customer", () -> {
            Show data = createShow(); loginAs("customer"); return data;
        });
        evidence.step("F03-schedule", "Filter movie/date and follow correct showtime; reference F03_Schedule_015", () -> {
            ShowtimesPage schedules = new ShowtimesPage(driver, baseUrl);
            schedules.openForMovie(show.movieId());
            schedules.filterAndBook(show);
        });
    }
}
