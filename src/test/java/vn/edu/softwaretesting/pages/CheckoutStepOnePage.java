package vn.edu.softwaretesting.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CheckoutStepOnePage {

    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By CANCEL_BUTTON = By.id("cancel");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public CheckoutStepOnePage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CheckoutStepOnePage open() {
        driver.get(baseUrl + "/checkout-step-one.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME));
        return this;
    }

    public CheckoutStepOnePage enterInformation(String firstName, String lastName, String postalCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIRST_NAME));

        WebElement fn = driver.findElement(FIRST_NAME);
        fn.clear();
        if (firstName != null && !firstName.isEmpty()) {
            fn.sendKeys(firstName);
        }

        WebElement ln = driver.findElement(LAST_NAME);
        ln.clear();
        if (lastName != null && !lastName.isEmpty()) {
            ln.sendKeys(lastName);
        }

        WebElement pc = driver.findElement(POSTAL_CODE);
        pc.clear();
        if (postalCode != null && !postalCode.isEmpty()) {
            pc.sendKeys(postalCode);
        }
        return this;
    }

    public void clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
    }

    public void clickCancel() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CANCEL_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();
    }

    public CheckoutStepTwoPage proceedToStepTwo(String firstName, String lastName, String postalCode) {
        enterInformation(firstName, lastName, postalCode);
        clickContinue();
        return new CheckoutStepTwoPage(driver, baseUrl);
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).getText();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isOnStepOne() {
        try {
            return wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));
        } catch (TimeoutException e) {
            return driver.getCurrentUrl().contains("checkout-step-one.html");
        }
    }
}