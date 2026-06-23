package com.demoshop.components;

import com.demoshop.base.BasePage;
import com.demoshop.pages.DigitalDownloadsPage;
import com.demoshop.pages.RegisterPage;
import com.demoshop.pages.ShoppingCartPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/**
 * The masthead present on every page: account links, the cart counter, and the
 * top category navigation. Modelled as a component (not a page) because it is
 * shared across pages rather than owned by any single one.
 */
public class HeaderComponent extends BasePage {

    private static final By REGISTER_LINK = By.cssSelector(".ico-register");
    private static final By ACCOUNT_EMAIL = By.cssSelector(".header-links a.account");
    private static final By CART_QUANTITY = By.cssSelector(".header-links .cart-qty");
    private static final By DIGITAL_DOWNLOADS_LINK =
            By.cssSelector(".top-menu a[href='/digital-downloads']");
    private static final By SHOPPING_CART_LINK = By.cssSelector(".header-links a.ico-cart");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    public RegisterPage openRegister() {
        click(REGISTER_LINK);
        return new RegisterPage(driver);
    }

    /** The logged-in customer's e-mail, shown in the header once authenticated. */
    public String getLoggedInEmail() {
        return getText(ACCOUNT_EMAIL);
    }

    public DigitalDownloadsPage openDigitalDownloads() {
        click(DIGITAL_DOWNLOADS_LINK);
        return new DigitalDownloadsPage(driver);
    }

    public ShoppingCartPage openShoppingCart() {
        // Clear the post-add success toast first: it overlays the header and would
        // otherwise intercept the click on the cart link.
        dismissNotificationBar();
        click(SHOPPING_CART_LINK);
        return new ShoppingCartPage(driver);
    }

    /**
     * Waits for the header cart counter to reach {@code expected} (e.g. "(1)").
     * The counter updates asynchronously after an add-to-cart AJAX call, so this
     * polls rather than reading once.
     *
     * @return {@code true} if the counter reached the expected value in time.
     */
    public boolean cartQuantityIs(String expected) {
        try {
            return waitForText(CART_QUANTITY, expected);
        } catch (TimeoutException e) {
            // Narrow catch: turn the timeout into a clean assertion failure with a
            // descriptive message, rather than surfacing a raw stack trace.
            return false;
        }
    }
}
