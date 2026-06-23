package com.demoshop.base;

import com.demoshop.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Shared interaction layer for all page objects. Every interaction goes through a
 * single, condition-based explicit-wait strategy — there is deliberately no
 * {@code Thread.sleep} anywhere in the suite.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.EXPLICIT_WAIT);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement field = waitForVisible(locator);
        field.clear();
        field.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /** Waits for an element's text to equal {@code expected}; useful for async UI updates. */
    protected boolean waitForText(By locator, String expected) {
        return wait.until(ExpectedConditions.textToBe(locator, expected));
    }

    protected boolean isVisible(By locator) {
        return waitForVisible(locator).isDisplayed();
    }

    private static final By NOTIFICATION_BAR = By.id("bar-notification");
    private static final By NOTIFICATION_CLOSE = By.cssSelector("#bar-notification .close");

    /**
     * Closes the nopCommerce {@code #bar-notification} toast if it's showing.
     * <p>
     * After an add-to-cart the green success toast is rendered at the top of the
     * page and overlays the header links; clicking e.g. the cart link while it's up
     * throws {@code ElementClickInterceptedException}. Dismissing it first mirrors
     * what a real user does and keeps header navigation reliable. No-op when no bar
     * is present, so it's safe to call unconditionally before a header click.
     */
    protected void dismissNotificationBar() {
        List<WebElement> bar = driver.findElements(NOTIFICATION_BAR);
        if (bar.isEmpty() || !bar.get(0).isDisplayed()) {
            return;
        }
        List<WebElement> close = driver.findElements(NOTIFICATION_CLOSE);
        if (!close.isEmpty()) {
            close.get(0).click();
        }
        wait.until(ExpectedConditions.invisibilityOfElementLocated(NOTIFICATION_BAR));
    }
}
