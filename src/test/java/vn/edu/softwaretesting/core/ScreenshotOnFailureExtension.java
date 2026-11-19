package vn.edu.softwaretesting.core;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    private static final String EVIDENCE_DIR = "evidence";

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Optional<Throwable> exception = context.getExecutionException();
        
        if (exception.isPresent()) {
            captureScreenshot(context);
        }
    }

    private void captureScreenshot(ExtensionContext context) {
        Object testInstance = context.getRequiredTestInstance();

        WebDriver driver = null;
        if (testInstance instanceof WebTestBase webTestBase) {
            driver = webTestBase.getDriver();
        }

        if (driver == null) {
            System.err.println("[SCREENSHOT] Không tìm thấy WebDriver để chụp màn hình.");
            return;
        }

        try {
            if (driver instanceof TakesScreenshot takesScreenshot) {
                File screenshot = takesScreenshot.getScreenshotAs(OutputType.FILE);

                Path outputFolder = Paths.get(EVIDENCE_DIR);
                if (!Files.exists(outputFolder)) {
                    Files.createDirectories(outputFolder);
                }

                String className = context.getRequiredTestClass().getSimpleName();
                String methodName = context.getRequiredTestMethod().getName();
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

                
                String fileName = String.format("%s_%s_%s_FAIL.png", className, methodName, timestamp);
                Path destination = outputFolder.resolve(fileName);

                Files.copy(screenshot.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[SCREENSHOT] Đã lưu ảnh chụp lỗi: " + destination.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[SCREENSHOT] Lỗi khi chụp ảnh màn hình: " + e.getMessage());
        }
    }
}