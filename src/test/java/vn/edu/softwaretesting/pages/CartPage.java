package vn.edu.softwaretesting.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By checkoutButton = By.id("checkout");
    private final By cartItem = By.className("cart_item");

    public CartPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isOnCart() {
        try {
            return wait.until(ExpectedConditions.urlContains("cart.html"));
        } catch (TimeoutException e) {
            return driver.getCurrentUrl().contains("cart.html");
        }
    }

    public boolean hasProducts() {
        try {
            return !wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cartItem)).isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public CheckoutStepOnePage checkout() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
        return new CheckoutStepOnePage(driver, baseUrl);
    }
}