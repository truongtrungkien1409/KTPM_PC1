package vn.edu.softwaretesting.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CartPage {

    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CART_ITEM = By.className("cart_item");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public CartPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Nhấn nút Checkout để chuyển sang trang nhập thông tin thanh toán */
    public CheckoutStepOnePage checkout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT_BUTTON)).click();
        return new CheckoutStepOnePage(driver, baseUrl);
    }

    /** Kiểm tra URL hiện tại có chứa cart.html hay không (có chờ) */
    public boolean isOnCart() {
        try {
            wait.until(ExpectedConditions.urlContains("cart.html"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Kiểm tra giỏ hàng có sản phẩm hay không (dùng explicit wait) */
    public boolean hasProducts() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(CART_ITEM));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
