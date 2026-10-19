package vn.edu.softwaretesting.tests;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.LoginPage;
import vn.edu.softwaretesting.pages.LoginPage.LoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceTest extends WebTestBase {

    @Test
    @Tag("known-bug")
    @DisplayName("TC-NFR-01 [BUG-01]: inventory loads within 3 s")
    void inventoryLoadsWithinThreeSeconds() {
        long start = System.nanoTime();
        LoginResult result = new LoginPage(driver, baseUrl)
                .open()
                .login("performance_glitch_user", "secret_sauce");
        long millis = Duration.ofNanos(System.nanoTime() - start).toMillis();

        assertTrue(result.accepted());
        // NFR-01 yêu cầu tải trong vòng <= 3 giây (3000 ms), test này sẽ FAIL và sinh BUG-01
        assertTrue(millis <= 3000, () -> "Inventory took " + millis + " ms (Exceeds NFR-01 threshold)");
    }
}