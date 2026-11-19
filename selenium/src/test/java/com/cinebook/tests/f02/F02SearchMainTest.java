package com.cinebook.tests.f02;

import com.cinebook.base.FeatureMainTest;
import com.cinebook.pages.MoviesPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class F02SearchMainTest extends FeatureMainTest {
    @Test
    @DisplayName("F02 main | Search movie and verify its card")
    void searchMovieByTitle() {
        MovieData movie = prepare("Create a unique movie and log in as Customer", () -> {
            MovieData data = createMovie(); loginAs("customer"); return data;
        });
        evidence.step("F02-search", "Find one matching movie, title/duration/rating/genre/status; reference F02_Search_002", () ->
                new MoviesPage(driver, baseUrl).search(movie.title()));
    }
}
