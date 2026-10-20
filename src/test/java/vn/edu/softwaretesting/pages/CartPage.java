package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CartPage {
    private final WebDriver driver;
    private final String baseUrl;
    private final WebDriverWait wait;

    private final By cartItems = By.cssSelector(".cart_item");
    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By removeButtons = By.xpath("//button[text()='Remove']");

    public CartPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CartPage open() {
        driver.get(baseUrl + "/cart.html");
        return this;
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public List<String> getCartItemNames() {
        return driver.findElements(itemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public CartPage removeFirstItem() {
        List<WebElement> buttons = driver.findElements(removeButtons);
        if (!buttons.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(buttons.get(0))).click();
        }
        return this;
    }

    public CartPage removeItemByName(String productName) {
        String xpath = String.format(
                "//div[text()='%s']/ancestor::div[@class='cart_item']//button[text()='Remove']", 
                productName);
        WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        removeButton.click();
        return this;
    }

    public void clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
    }

    public void clickContinueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingButton)).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}