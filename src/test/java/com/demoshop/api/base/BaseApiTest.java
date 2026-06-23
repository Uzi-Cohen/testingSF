package com.demoshop.api.base;

import com.demoshop.api.mock.MockBackend;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static io.restassured.config.LogConfig.logConfig;

/**
 * Lifecycle for every API/backend test: a programmable mock backend (WireMock) is
 * started once per class on a random port, re-stubbed with the default mock data
 * before each test (isolation), and stopped at the end. REST Assured is configured
 * to print the request and response <em>only</em> when an expectation fails, which
 * keeps passing runs quiet and failing runs diagnosable.
 * <p>
 * These tests are fully offline and deterministic — no live site, no browser, no
 * shared state — the fast base of the test pyramid beneath the UI E2E suite.
 */
public abstract class BaseApiTest {

    static {
        // Global, constant, set-once config (safe under parallel classes): dump the
        // full request/response to the log on any failed expectation.
        RestAssured.config = RestAssured.config().logConfig(
                logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    private MockBackend backend;

    /** Per-test request spec: points at this class's mock backend and speaks JSON. */
    protected RequestSpecification rest;

    @BeforeClass(alwaysRun = true)
    public void startBackend() {
        backend = new MockBackend();
        backend.start();
    }

    @AfterClass(alwaysRun = true)
    public void stopBackend() {
        if (backend != null) {
            backend.stop();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void resetBackendAndSpec() {
        backend.reset(); // re-applies the default mock data, so each test starts clean
        rest = new RequestSpecBuilder()
                .setBaseUri(backend.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }
}
