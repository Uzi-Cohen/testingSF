package com.demoshop.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates and supplies a {@link WebDriver} per thread, so the suite stays
 * thread-safe when tests run in parallel. Driver binaries are resolved
 * automatically by Selenium Manager (built into Selenium 4.6+), so there is no
 * chromedriver to download, version-match, or commit.
 * <p>
 * This is the single seam to extend for remote execution: swapping the local
 * {@code ChromeDriver} for a {@code RemoteWebDriver} pointed at a Grid / Selenoid
 * / Perfecto endpoint is a change in this class only.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void createDriver() {
        DRIVER.set(buildDriver());
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver buildDriver() {
        return switch (Config.BROWSER) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                if (Config.HEADLESS) {
                    options.addArguments("--headless=new", "--window-size=1920,1080");
                }
                options.addArguments("--remote-allow-origins=*");
                yield new ChromeDriver(options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (Config.HEADLESS) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: '" + Config.BROWSER + "'. Use 'chrome' or 'firefox'.");
        };
    }
}
