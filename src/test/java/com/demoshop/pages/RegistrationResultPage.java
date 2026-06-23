package com.demoshop.pages;

import com.demoshop.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Confirmation page shown immediately after a successful registration.
 */
public class RegistrationResultPage extends BasePage {

    private static final By RESULT_MESSAGE = By.cssSelector("div.result");
    private static final By CONTINUE_BUTTON = By.cssSelector("input.register-continue-button");

    public RegistrationResultPage(WebDriver driver) {
        super(driver);
    }

    public String getResultMessage() {
        return getText(RESULT_MESSAGE);
    }

    public void clickContinue() {
        click(CONTINUE_BUTTON);
    }
}
