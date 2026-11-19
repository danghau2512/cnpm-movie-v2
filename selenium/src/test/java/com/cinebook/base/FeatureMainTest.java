package com.cinebook.base;

import com.cinebook.pages.*;
import com.cinebook.pages.AdminShowtimesPage.Show;
import com.cinebook.pages.BookingPage.Seat;
import com.cinebook.utils.FlowEvidence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Shared UI preconditions only. Each F folder owns its actual @Test methods. */
public abstract class FeatureMainTest extends BaseTest {
    protected FlowEvidence evidence;
    protected LoginPage login;
    protected record MovieData(int id, String title) {}
    protected record OrderData(Show show, Seat seat, int id, String code) {}

    @BeforeEach
    void prepareFeature(TestInfo info) {
        login = new LoginPage(driver, baseUrl);
        evidence = new FlowEvidence(driver, getClass().getSimpleName() + "-" + info.getTestMethod().orElseThrow().getName(),
                Boolean.parseBoolean(config.get("flow.screenshots", "true")));
    }

    protected <T> T prepare(String description, Supplier<T> factory) {
        AtomicReference<T> value = new AtomicReference<>();
        evidence.step("SETUP", description, () -> value.set(factory.get()));
        return value.get();
    }

    protected void loginAs(String role) {
        login.logout();
        login.login(config.account(role), role.equals("admin"));
    }

    protected MovieData createMovie() {
        loginAs("admin");
        String title = "SEL_MAIN_" + UUID.randomUUID().toString().replace("-", "");
        return new MovieData(new AdminMoviesPage(driver, baseUrl).create(title), title);
    }

    protected Show createShow(MovieData movie) {
        int days = Integer.parseInt(config.get("flow.daysAhead", "14"));
        assertTrue(days >= 1, "flow.daysAhead must be positive");
        return new AdminShowtimesPage(driver, baseUrl).create(movie.id(), movie.title(), config.get("flow.roomId", "1"), days);
    }

    protected Show createShow() { return createShow(createMovie()); }

    protected OrderData createUnpaidOrder() {
        Show show = createShow();
        loginAs("customer");
        BookingPage booking = new BookingPage(driver, baseUrl);
        booking.openShow(show);
        Seat seat = booking.selectFirstAvailable(show);
        return new OrderData(show, seat, booking.submit(), null);
    }

    protected OrderData createCounterOrder() {
        OrderData order = createUnpaidOrder();
        String code = new PaymentPage(driver, baseUrl).payAtCounter(order.id(), order.show(), order.seat());
        return new OrderData(order.show(), order.seat(), order.id(), code);
    }
}
