package com.demoshop.tests;

import com.demoshop.base.BaseTest;
import com.demoshop.config.Config;
import com.demoshop.model.User;
import com.demoshop.pages.RegisterPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Negative coverage beyond the assigned happy path: the application must reject a
 * second registration that reuses an existing e-mail. Self-contained — it
 * registers a user, then re-registers with the same e-mail in a single flow, so
 * it depends on no external state and can run in any order.
 */
public class RegistrationNegativeTest extends BaseTest {

    @Test(description = "Registering with an e-mail that already exists is rejected")
    public void register_withExistingEmail_isRejected() {
        User user = User.randomUser();

        // Arrange: register once so this e-mail is now taken.
        header.openRegister().registerAs(user);

        // Act: go back to the form and submit the same e-mail again.
        driver.get(Config.BASE_URL + "/register");
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.submitExpectingError(user);

        // Assert: the application surfaces the duplicate-email error.
        Assert.assertEquals(registerPage.getErrorMessage(),
                "The specified email already exists",
                "Duplicate-email registration should have been rejected");
    }
}
