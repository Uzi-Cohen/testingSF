package com.demoshop.pages;

import com.demoshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Shopping cart page (/cart). Owns the line-item table so a test can assert which
 * products actually reached the cart — the final validation the brief asks for
 * ("verify the product name in the cart matches the one selected").
 */
public class ShoppingCartPage extends BasePage {

    // nopCommerce renders the cart as a table of `.cart-item-row`s; the product
    // name in each row is the `a.product-name` link.
    private static final By CART_ITEM_ROW = By.cssSelector(".cart tr.cart-item-row");
    private static final By PRODUCT_NAME = By.cssSelector("td.product a.product-name");

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
    }

    /** Product names currently listed in the cart, in display order. */
    public List<String> productNames() {
        return waitForAllVisible(CART_ITEM_ROW).stream()
                .map(row -> row.findElement(PRODUCT_NAME).getText().trim())
                .toList();
    }

    /** True if a line item with exactly this product name is in the cart. */
    public boolean contains(String productName) {
        return productNames().contains(productName);
    }
}
