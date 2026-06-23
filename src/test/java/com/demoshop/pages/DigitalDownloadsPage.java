package com.demoshop.pages;

import com.demoshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Digital Downloads category page. Owns the product grid and the logic to pick a
 * product at random and add it to the cart.
 */
public class DigitalDownloadsPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(DigitalDownloadsPage.class);

    // nopCommerce wraps each product card in `.item-box` — one box per product.
    private static final By PRODUCT_ITEM = By.cssSelector(".product-grid .item-box");
    private static final By PRODUCT_TITLE = By.cssSelector(".product-title a");
    private static final By ADD_TO_CART_BUTTON =
            By.cssSelector("input.product-box-add-to-cart-button");
    private static final By SUCCESS_NOTIFICATION = By.cssSelector("#bar-notification.success");

    public DigitalDownloadsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Picks a random product on the page, adds it to the cart, and returns the
     * name of the product chosen so the test log and any failure message can
     * identify exactly which product was exercised on this run.
     */
    public String addRandomProductToCart() {
        List<WebElement> products = waitForAllVisible(PRODUCT_ITEM);
        int index = ThreadLocalRandom.current().nextInt(products.size());
        WebElement product = products.get(index);

        String productName = product.findElement(PRODUCT_TITLE).getText().trim();
        log.info("Adding random product to cart: '{}' (item {} of {} on the grid)",
                productName, index + 1, products.size());
        product.findElement(ADD_TO_CART_BUTTON).click();
        return productName;
    }

    /** True once the green "added to your shopping cart" bar is shown. */
    public boolean isAddToCartConfirmationDisplayed() {
        return isVisible(SUCCESS_NOTIFICATION);
    }

    public String getConfirmationText() {
        return getText(SUCCESS_NOTIFICATION);
    }
}
