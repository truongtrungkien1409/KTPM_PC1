package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class InventoryPage {

    private static final By PRODUCT_SORT_CONTAINER = By.cssSelector("[data-test='product-sort-container']");
    private static final By ITEM_NAME = By.className("inventory_item_name");
    private static final By ITEM_PRICE = By.className("inventory_item_price");
    private static final By INVENTORY_ITEMS = By.cssSelector(".inventory_item");
    private static final By SHOPPING_CART_BADGE = By.cssSelector(".shopping_cart_badge");
    private static final By SHOPPING_CART_LINK = By.cssSelector(".shopping_cart_link");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public InventoryPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    public InventoryPage open() {
        driver.get(baseUrl + "/inventory.html");
        return this;
    }

    // Các hàm sắp xếp và lấy dữ liệu (từ nhánh main)
    public InventoryPage sortBy(String optionValue) {
        WebElement sortDropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(PRODUCT_SORT_CONTAINER));
        Select select = new Select(sortDropdown);
        select.selectByValue(optionValue);
        return this;
    }

    public List<String> names() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ITEM_NAME));
        return driver.findElements(ITEM_NAME)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    public List<Double> prices() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ITEM_PRICE));
        return driver.findElements(ITEM_PRICE)
                .stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "").trim()))
                .toList();
    }

    // Các hàm quản lý giỏ hàng (từ nhánh Ngọc)
    public int getItemCount() {
        return driver.findElements(INVENTORY_ITEMS).size();
    }

    public InventoryPage addItemToCartByName(String productName) {
        String xpath = String.format(
                "//div[text()='%s']/ancestor::div[@class='inventory_item']//button",
                productName);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        button.click();
        return this;
    }

    public InventoryPage removeItemByName(String productName) {
        String xpath = String.format(
                "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[text()='Remove']",
                productName);
        WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        removeButton.click();
        return this;
    }

    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(SHOPPING_CART_BADGE);
        if (badges.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(badges.get(0).getText().trim());
    }

    public boolean isBadgeDisplayed() {
        return !driver.findElements(SHOPPING_CART_BADGE).isEmpty();
    }

    public CartPage openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(SHOPPING_CART_LINK)).click();
        return new CartPage(driver, baseUrl);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}