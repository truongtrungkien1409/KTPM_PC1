package vn.edu.softwaretesting.tests;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.LoginPage;
import vn.edu.softwaretesting.pages.LoginPage.LoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceTest extends WebTestBase {

    private static final int RUNS = 5;

    @Test
    @Tag("known-bug")
    @DisplayName("TC-NFR-01 [BUG-01]: inventory loads within 3 s (median of 5)")
    void inventoryLoadsWithinThreeSeconds() {
        List<Long> samples = new ArrayList<>();

        for (int i = 0; i < RUNS; i++) {
            driver.manage().deleteAllCookies();
            LoginPage loginPage = new LoginPage(driver, baseUrl).open();

            long start = System.nanoTime();
            LoginResult result = loginPage.login("performance_glitch_user", "secret_sauce");
            long millis = Duration.ofNanos(System.nanoTime() - start).toMillis();

            assertTrue(result.accepted(), "Login must be accepted on run " + (i + 1));
            samples.add(millis);
        }

        Collections.sort(samples);
        long median = samples.get(RUNS / 2);

        // NFR-01 yêu cầu tải trong vòng <= 3 giây (3000 ms).
        // Test này sẽ FAIL và sinh BUG-01, nên gán @Tag("known-bug"), không được nới ngưỡng.
        assertTrue(median <= 3000,
                () -> "Inventory median took " + median + " ms (Exceeds NFR-01 threshold)");
    }
}