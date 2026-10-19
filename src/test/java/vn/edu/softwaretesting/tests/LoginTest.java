package vn.edu.softwaretesting.tests;

import vn.edu.softwaretesting.core.WebTestBase;
import vn.edu.softwaretesting.pages.LoginPage;
import vn.edu.softwaretesting.pages.LoginPage.LoginResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends WebTestBase {

    @ParameterizedTest(name = "TC-LOGIN-{index}: user={0}, accepted={2}")
    @CsvFileSource(resources = "/login-data.csv", numLinesToSkip = 1)
    @DisplayName("FR-01: Login Parameterized Test")
    void loginShouldMatchExpectedOutcome(String username, String password, boolean expectedAccepted, String expectedEvidence) {
        // Xử lý giá trị rỗng từ CSV (nếu chuỗi rỗng thì chuyển thành "")
        String user = (username == null) ? "" : username;
        String pass = (password == null) ? "" : password;

        LoginResult result = new LoginPage(driver, baseUrl)
                .open()
                .login(user, pass);

        assertEquals(expectedAccepted, result.accepted(), "Login acceptance mismatch for user: " + user);

        if (expectedAccepted) {
            assertTrue(driver.getCurrentUrl().contains(expectedEvidence),
                    () -> "Expected URL to contain: " + expectedEvidence + ", but got: " + driver.getCurrentUrl());
        } else {
            assertTrue(result.errorMessage().contains(expectedEvidence),
                    () -> "Expected error message to contain: " + expectedEvidence + ", but got: " + result.errorMessage());
        }
    }
}