package com.demoshop.pages;

import com.demoshop.base.BasePage;
import com.demoshop.model.User;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Customer registration form (/register). Every locator is a stable element ID
 * exposed by the application, so no XPath or brittle structural selectors are
 * needed.
 */
public class RegisterPage extends BasePage {

    private static final By GENDER_MALE = By.id("gender-male");
    private static final By FIRST_NAME = By.id("FirstName");
    private static final By LAST_NAME = By.id("LastName");
    private static final By EMAIL = By.id("Email");
    private static final By PASSWORD = By.id("Password");
    private static final By CONFIRM_PASSWORD = By.id("ConfirmPassword");
    private static final By REGISTER_BUTTON = By.id("register-button");
    private static final By VALIDATION_ERROR = By.cssSelector(".validation-summary-errors li");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    /** Fills the whole form from a {@link User} and submits; returns the result page. */
    public RegistrationResultPage registerAs(User user) {
        fillForm(user);
        click(REGISTER_BUTTON);
        return new RegistrationResultPage(driver);
    }

    /** Submits the form expecting it to stay put with a validation error (negative path). */
    public RegisterPage submitExpectingError(User user) {
        fillForm(user);
        click(REGISTER_BUTTON);
        return this;
    }

    public String getErrorMessage() {
        return getText(VALIDATION_ERROR);
    }

    private void fillForm(User user) {
        click(GENDER_MALE);
        type(FIRST_NAME, user.firstName());
        type(LAST_NAME, user.lastName());
        type(EMAIL, user.email());
        type(PASSWORD, user.password());
        type(CONFIRM_PASSWORD, user.password());
    }
}
