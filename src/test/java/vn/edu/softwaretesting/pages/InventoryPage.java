package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class InventoryPage {

    private static final By CART_BADGE = By.className("shopping_cart_badge");
    private static final By CART_LINK = By.className("shopping_cart_link");
    private static final By SORT_CONTAINER = By.className("product_sort_container");
    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_PRICE = By.className("inventory_item_price");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public InventoryPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.baseUrl = null;
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

    /** Thêm sản phẩm vào giỏ hàng theo tên sản phẩm */
    public InventoryPage addItemToCartByName(String productName) {
        String slug;
        if (productName.contains("Test.allTheThings")) {
            slug = "test.allthethings()-t-shirt-(red)";
        } else {
            slug = productName.toLowerCase()
                    .replace(" ", "-")
                    .replace("(", "")
                    .replace(")", "")
                    .replace(".", "");
        }
        return addToCart(slug);
    }

    /** Lấy số lượng sản phẩm hiển thị trên badge giỏ hàng */
    public int getCartBadgeCount() {
        try {
            String badgeText = wait.until(ExpectedConditions.visibilityOfElementLocated(CART_BADGE)).getText();
            return Integer.parseInt(badgeText);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Sắp xếp sản phẩm theo option (az, za, lohi, hilo) */
    public InventoryPage sortBy(String option) {
        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(SORT_CONTAINER));
        new Select(sortDropdown).selectByValue(option);
        return this;
    }

    /** Lấy danh sách tên sản phẩm hiện tại trên trang */
    public List<String> names() {
        List<WebElement> nameElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_NAME));
        List<String> names = new ArrayList<>();
        for (WebElement el : nameElements) {
            names.add(el.getText());
        }
        return names;
    }

    /** Lấy danh sách giá sản phẩm hiện tại trên trang */
    public List<Double> prices() {
        List<WebElement> priceElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(ITEM_PRICE));
        List<Double> prices = new ArrayList<>();
        for (WebElement el : priceElements) {
            String priceText = el.getText().replace("$", "").trim();
            prices.add(Double.parseDouble(priceText));
        }
        return prices;
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
