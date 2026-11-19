package com.cinebook.tests.f07;

import com.cinebook.base.FeatureMainTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class F07ShowtimeMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F07 main | Admin creates a future OPEN showtime")
    void addFutureShowtime() {
        MovieData movie = prepare("Prepare a movie as Admin", this::createMovie);
        evidence.step("F07-create", "Save/read back future OPEN show in active room; reference F07_Showtime_003", () ->
                createShow(movie));
    }
}
