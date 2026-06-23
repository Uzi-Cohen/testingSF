package com.demoshop.base;

import com.demoshop.components.HeaderComponent;
import com.demoshop.config.Config;
import com.demoshop.config.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Lifecycle shared by every test: a fresh browser per method (full isolation, so
 * tests run alone or in any order), navigation to the base URL, and a guaranteed
 * teardown even when a test fails.
 */
public abstract class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected HeaderComponent header;

    @BeforeMethod(alwaysRun = true)
    public void setUp(java.lang.reflect.Method method) {
        log.info("=== START {} === [browser={}, headless={}, baseUrl={}]",
                method.getName(), Config.BROWSER, Config.HEADLESS, Config.BASE_URL);
        DriverFactory.createDriver();
        driver = DriverFactory.getDriver();
        driver.manage().window().maximize();
        driver.get(Config.BASE_URL);
        header = new HeaderComponent(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        log.info("=== END {} === [{}]", result.getMethod().getMethodName(),
                result.isSuccess() ? "PASSED" : "FAILED");
        DriverFactory.quitDriver();
    }
}
