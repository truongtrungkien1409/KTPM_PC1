package vn.edu.softwaretesting.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CheckoutStepTwoPage {

    private static final By ITEM_TOTAL_LABEL = By.className("summary_subtotal_label");
    private static final By TAX_LABEL = By.className("summary_tax_label");
    private static final By TOTAL_LABEL = By.className("summary_total_label");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By CANCEL_BUTTON = By.id("cancel");
    private static final By COMPLETE_HEADER = By.className("complete-header");
    private static final By BACK_HOME_BUTTON = By.id("back-to-products");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    public CheckoutStepTwoPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isOnStepTwo() {
        try {
            return wait.until(ExpectedConditions.urlContains("checkout-step-two.html"))
                    && wait.until(ExpectedConditions.visibilityOfElementLocated(ITEM_TOTAL_LABEL)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public double getItemTotal() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ITEM_TOTAL_LABEL));
        String text = driver.findElement(ITEM_TOTAL_LABEL).getText();
        return parsePrice(text);
    }

    public double getTax() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(TAX_LABEL));
        String text = driver.findElement(TAX_LABEL).getText();
        return parsePrice(text);
    }

    public double getTotal() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(TOTAL_LABEL));
        String text = driver.findElement(TOTAL_LABEL).getText();
        return parsePrice(text);
    }

    public void clickFinish() {
        WebElement finishBtn = wait.until(ExpectedConditions.elementToBeClickable(FINISH_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", finishBtn);
        finishBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER));
    }

    public void clickCancel() {
        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(CANCEL_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cancelBtn);
        cancelBtn.click();
    }

    public String getCompleteMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER)).getText();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isOrderCompleted() {
        try {
            return wait.until(ExpectedConditions.urlContains("checkout-complete.html"))
                    && wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void clickBackHome() {
        WebElement backHomeBtn = wait.until(ExpectedConditions.elementToBeClickable(BACK_HOME_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backHomeBtn);
        backHomeBtn.click();
    }

    private double parsePrice(String text) {
        int dollarIndex = text.indexOf('$');
        if (dollarIndex != -1) {
            String numberPart = text.substring(dollarIndex + 1).trim();
            return Double.parseDouble(numberPart);
        }
        throw new IllegalArgumentException("Khong tim thay ky tu $ trong chuoi gia: " + text);
    }
}