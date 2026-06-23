package com.demoshop.listeners;

import com.demoshop.config.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IConfigurationListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a screenshot the moment a test fails, so a CI failure is diagnosable
 * from an image rather than only a stack trace. Images land in
 * {@code target/screenshots/} (cleaned by {@code mvn clean}) and are uploaded as
 * build artifacts by CI.
 * <p>
 * Both a test-body failure ({@link #onTestFailure}) and a setup failure
 * ({@link #onConfigurationFailure}, e.g. the browser or home page not loading) are
 * covered, because either is worth a picture. The driver is pulled from
 * {@link DriverFactory} (ThreadLocal), so this works correctly under parallel runs.
 */
public class ScreenshotListener implements ITestListener, IConfigurationListener {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotListener.class);
    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");
    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    @Override
    public void onTestFailure(ITestResult result) {
        capture(testName(result));
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        capture(testName(result));
    }

    private void capture(String label) {
        WebDriver driver = DriverFactory.getDriver();
        if (!(driver instanceof TakesScreenshot shotter)) {
            // No live browser (e.g. the driver itself failed to start) — nothing to capture.
            log.warn("No screenshot captured for '{}': no live browser session", label);
            return;
        }
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            Path target = SCREENSHOT_DIR.resolve(
                    "%s-%s.png".formatted(label, LocalDateTime.now().format(STAMP)));
            Files.write(target, shotter.getScreenshotAs(OutputType.BYTES));
            log.error("'{}' failed — screenshot saved to {}", label, target.toAbsolutePath());
        } catch (IOException e) {
            // A screenshot failure must never mask the real test failure.
            log.warn("Could not save failure screenshot for '{}': {}", label, e.getMessage());
        }
    }

    private static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + "." + result.getMethod().getMethodName();
    }
}
