package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InventoryPage {
    private final WebDriver driver;
    private final String baseUrl;
    private final WebDriverWait wait;

    private final By inventoryItems = By.cssSelector(".inventory_item");
    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By shoppingCartBadge = By.cssSelector(".shopping_cart_badge");
    private final By shoppingCartLink = By.cssSelector(".shopping_cart_link");

    public InventoryPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public InventoryPage open() {
        driver.get(baseUrl + "/inventory.html");
        return this;
    }

    public int getItemCount() {
        return driver.findElements(inventoryItems).size();
    }

    public List<String> getItemNames() {
        return driver.findElements(itemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
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
        List<WebElement> badges = driver.findElements(shoppingCartBadge);
        if (badges.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(badges.get(0).getText().trim());
    }

    public boolean isBadgeDisplayed() {
        return !driver.findElements(shoppingCartBadge).isEmpty();
    }

    public CartPage openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(shoppingCartLink)).click();
        return new CartPage(driver, baseUrl);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}