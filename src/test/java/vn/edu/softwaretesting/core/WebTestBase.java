package vn.edu.softwaretesting.core;

import java.time.Duration;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public abstract class WebTestBase {

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeEach
    void openBrowser() {
        baseUrl = System.getProperty("baseUrl", "https://www.saucedemo.com");
        String browser = System.getProperty("browser", "chrome")
                .toLowerCase(Locale.ROOT);
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", "true"));

        driver = switch (browser) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser);
        };

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().setSize(
                new org.openqa.selenium.Dimension(1440, 900));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Bắt ngoại lệ nếu session trình duyệt đã bị đứt trước đó
            } finally {
                driver = null;
            }
        }
    }

    private ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
            // Thêm window-size trực tiếp vào options để tránh crash ở headless mode
            options.addArguments("--window-size=1440,900");
        }

        // Cấu hình chống crash trình duyệt và ổn định tài nguyên
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-search-engine-choice-screen");
        options.addArguments("--log-level=3"); // Chỉ hiển thị lỗi nghiêm trọng (Severe Errors)
        System.setProperty("webdriver.chrome.silentOutput", "true"); // Tắt log khởi động Driver
        return options;
    }

    private FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
            options.addArguments("--width=1440");
            options.addArguments("--height=900");
        }
        return options;
    }
}