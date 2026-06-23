package com.demoshop.tests;

import com.demoshop.base.BaseTest;
import com.demoshop.model.User;
import com.demoshop.pages.DigitalDownloadsPage;
import com.demoshop.pages.RegisterPage;
import com.demoshop.pages.RegistrationResultPage;
import com.demoshop.pages.ShoppingCartPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * End-to-end happy path: a new customer registers, lands logged-in, adds a
 * digital-download product to the cart, then opens the cart and confirms the
 * exact product they selected is there. Reads top-to-bottom as the feature it
 * verifies; the step numbers map 1:1 to the assignment brief (steps 1–11).
 */
public class RegistrationAndDigitalDownloadsTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(RegistrationAndDigitalDownloadsTest.class);

    @Test(description = "New customer registers, adds a digital-download product, "
            + "and the cart shows the exact product selected")
    public void newCustomer_registersAndAddsDigitalDownloadToCart() {
        User user = User.randomUser();
        log.info("Registering new customer: {}", user.email());

        // Steps 2–5: open the registration form, fill personal details + password, submit.
        RegisterPage registerPage = header.openRegister();
        RegistrationResultPage resultPage = registerPage.registerAs(user);

        Assert.assertEquals(resultPage.getResultMessage(), "Your registration completed",
                "Registration confirmation message was not displayed");

        // Step 6: leave the confirmation page.
        resultPage.clickContinue();

        // Step 7: the registered e-mail must now appear in the header (proves logged-in state).
        Assert.assertEquals(header.getLoggedInEmail(), user.email(),
                "Registered e-mail is not shown in the header — user is not logged in");
        log.info("Registration confirmed and user is logged in as {}", user.email());

        // Step 8: navigate to the Digital Downloads category.
        DigitalDownloadsPage digitalDownloads = header.openDigitalDownloads();

        // Step 9: add a random product and confirm the add was acknowledged.
        String addedProduct = digitalDownloads.addRandomProductToCart();

        Assert.assertTrue(digitalDownloads.isAddToCartConfirmationDisplayed(),
                "No success notification after adding '" + addedProduct + "' to the cart");
        Assert.assertTrue(
                digitalDownloads.getConfirmationText().toLowerCase().contains("shopping cart"),
                "Unexpected add-to-cart confirmation text for '" + addedProduct + "'");
        Assert.assertTrue(header.cartQuantityIs("(1)"),
                "Header cart counter did not update to (1) after adding '" + addedProduct + "'");

        // Step 10: open the shopping cart.
        ShoppingCartPage cart = header.openShoppingCart();

        // Step 11: the cart must contain the exact product that was selected — the
        // brief's final acceptance criterion. Asserting the *name*, not just the
        // counter, is what proves the right product reached the cart.
        Assert.assertTrue(cart.contains(addedProduct),
                "Cart does not contain the selected product '" + addedProduct
                        + "'. Cart actually holds: " + cart.productNames());
        log.info("Cart verified: it contains the selected product '{}'", addedProduct);
    }
}
