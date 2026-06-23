package com.demoshop.api.mock;

import com.demoshop.api.client.ApiConfig;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * A programmable mock of the shop's backend, built on WireMock. It serves the JSON
 * fixtures under {@code src/test/resources/mockdata/} for the endpoints the API
 * tests exercise, so those tests are deterministic and need no live server.
 * <p>
 * Each test class owns its own instance on a random port (parallel-safe), and
 * {@link #reset()} re-applies the default stubs before every test, so tests stay
 * isolated. Response templating is enabled so the registration stub can echo back
 * the e-mail it was sent — proving the request reached the backend, not a canned value.
 */
public final class MockBackend {

    private final WireMockServer server =
            new WireMockServer(options().dynamicPort().globalTemplating(true));

    public void start() {
        server.start();
        applyDefaultStubs();
    }

    public void stop() {
        server.stop();
    }

    /** Clears all stubs and request history, then re-applies the default mock data. */
    public void reset() {
        server.resetAll();
        applyDefaultStubs();
    }

    /** Base URL of this running mock, e.g. {@code http://localhost:54321}. */
    public String baseUrl() {
        return "http://localhost:" + server.port();
    }

    private void applyDefaultStubs() {
        // POST /register with a NEW e-mail -> 201 Created. The body echoes the e-mail
        // from the request (templating), so a test can prove its input round-tripped.
        server.stubFor(post(urlEqualTo(ApiConfig.REGISTER))
                .atPriority(5)
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture("registration-success.json"))));

        // POST /register with an e-mail that already exists -> 409 Conflict. Higher
        // priority (lower number) so it wins over the catch-all above when it matches.
        server.stubFor(post(urlEqualTo(ApiConfig.REGISTER))
                .atPriority(1)
                .withRequestBody(matchingJsonPath("$.email", equalTo(ApiConfig.EXISTING_EMAIL)))
                .willReturn(aResponse()
                        .withStatus(409)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture("registration-duplicate-email.json"))));

        // GET the Digital Downloads catalogue.
        server.stubFor(get(urlEqualTo(ApiConfig.CATALOG_DIGITAL_DOWNLOADS))
                .willReturn(okJson(fixture("digital-downloads-catalog.json"))));

        // POST an item to the cart -> 201 and the resulting cart.
        server.stubFor(post(urlEqualTo(ApiConfig.CART_ITEMS))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fixture("cart-with-item.json"))));

        // GET the current cart.
        server.stubFor(get(urlEqualTo(ApiConfig.CART))
                .willReturn(okJson(fixture("cart-with-item.json"))));
    }

    /** Loads a mock-data fixture from {@code mockdata/} on the test classpath. */
    private static String fixture(String fileName) {
        String path = "mockdata/" + fileName;
        try (InputStream in = MockBackend.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing mock fixture on classpath: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read mock fixture: " + path, e);
        }
    }
}
