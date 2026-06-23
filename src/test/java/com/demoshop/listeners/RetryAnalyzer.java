package com.demoshop.listeners;

import com.demoshop.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to {@link Config#RETRY_COUNT} times before reporting
 * it as failed. End-to-end tests against a live public site occasionally flake on
 * transient network or rendering hiccups; a single bounded retry absorbs that
 * noise without hiding a genuine, repeatable failure. Set {@code -Dretry.count=0}
 * to disable.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < Config.RETRY_COUNT) {
            attempts++;
            log.warn("Retrying '{}' (attempt {} of {}) after failure: {}",
                    result.getMethod().getMethodName(), attempts, Config.RETRY_COUNT,
                    result.getThrowable() == null ? "n/a" : result.getThrowable().getMessage());
            return true;
        }
        return false;
    }
}
