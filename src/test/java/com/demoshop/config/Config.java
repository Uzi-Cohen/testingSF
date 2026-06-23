package com.demoshop.config;

import java.time.Duration;

/**
 * Central, override-friendly test configuration.
 * <p>
 * Every value can be overridden from the command line for CI, e.g.:
 * <pre>
 *   mvn test -Dbrowser=firefox -Dheadless=true -Dtimeout=20
 * </pre>
 * Defaults are chosen so the suite runs locally with zero setup.
 */
public final class Config {

    private Config() {
        // Utility class — not instantiable.
    }

    public static final String BASE_URL =
            System.getProperty("baseUrl", "https://demowebshop.tricentis.com");

    public static final String BROWSER =
            System.getProperty("browser", "chrome").toLowerCase();

    public static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("headless", "false"));

    /** One suite-wide explicit-wait budget — a single wait strategy, not a patchwork. */
    public static final Duration EXPLICIT_WAIT =
            Duration.ofSeconds(Long.parseLong(System.getProperty("timeout", "15")));

    /**
     * How many times a failed test is retried before being reported as failed.
     * Absorbs transient live-site flake; set {@code -Dretry.count=0} to disable.
     */
    public static final int RETRY_COUNT =
            Integer.parseInt(System.getProperty("retry.count", "1"));
}
