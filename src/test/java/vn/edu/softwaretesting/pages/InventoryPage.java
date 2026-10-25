package vn.edu.softwaretesting.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class InventoryPage {

    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By CART_LINK = By.className("shopping_cart_link");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public InventoryPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Chờ trang inventory tải xong */
    public InventoryPage waitForPage() {
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        return this;
    }

    /** Nhấn nút Add to cart cho sản phẩm theo slug (ví dụ: "sauce-labs-backpack") */
    public InventoryPage addToCart(String productSlug) {
        By addButton = By.id("add-to-cart-" + productSlug);
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();
        return this;
    }

    /** Nhấn biểu tượng giỏ hàng để mở trang cart.html */
    public CartPage openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(CART_LINK)).click();
        return new CartPage(driver, baseUrl);
    }

    /** Kiểm tra URL hiện tại có chứa inventory.html hay không (có chờ) */
    public boolean isOnInventory() {
        try {
            wait.until(ExpectedConditions.urlContains("inventory.html"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Kiểm tra giỏ hàng rỗng (badge không hiển thị, dùng explicit wait) */
    public boolean isCartEmpty() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(CART_BADGE));
        } catch (Exception e) {
            return driver.findElements(CART_BADGE).isEmpty();
        }
    }
}
