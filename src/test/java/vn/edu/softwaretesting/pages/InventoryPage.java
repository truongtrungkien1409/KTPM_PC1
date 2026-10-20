package vn.edu.softwaretesting.pages;

import java.time.Duration;
import java.util.List;
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

    private final WebDriver driver;
    private final WebDriverWait wait;

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

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
}