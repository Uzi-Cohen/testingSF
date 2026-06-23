package com.demoshop.api.tests;

import com.demoshop.api.base.BaseApiTest;
import com.demoshop.api.client.ApiConfig;
import com.demoshop.api.model.ErrorResponse;
import com.demoshop.api.model.RegistrationRequest;
import com.demoshop.api.model.RegistrationResponse;
import com.demoshop.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;

/**
 * THE worked example for {@code API_TEST_STANDARD.md}. Every test below follows the
 * same 7-step recipe — (1) name, (2) arrange, (3) act, (4) assert status,
 * (5) assert body, (6) diagnosable messages, (7) isolated &amp; fast — so a new test
 * can be written by copying this shape.
 *
 * <p>The happy path is the canonical sample; the duplicate-e-mail case shows the very
 * same recipe applied to a negative scenario.
 */
public class RegistrationApiTest extends BaseApiTest {

    private static final Logger log = LoggerFactory.getLogger(RegistrationApiTest.class);

    /* (1) NAME — the method name states the behaviour: endpoint_condition_expectedResult. */
    @Test(description = "POST /register with a new e-mail returns 201 and confirms registration")
    public void register_withNewEmail_returns201AndConfirmsRegistration() {

        // (2) ARRANGE — isolated test data; the mock backend is already stubbed by BaseApiTest.
        User user = User.randomUser();
        RegistrationRequest request = RegistrationRequest.from(user);
        log.info("Registering via API: {}", user.email());

        // (3) ACT — exactly one call: the operation under test.
        RegistrationResponse response =
                given().spec(rest)
                        .body(request)
                .when()
                        .post(ApiConfig.REGISTER)
                .then()
                        // (4) ASSERT STATUS — check the status code first.
                        .statusCode(201)
                        // (5) ASSERT BODY — the response must honour the published JSON schema...
                        .body(matchesJsonSchemaInClasspath("schema/registration-response.schema.json"))
                        .extract().as(RegistrationResponse.class);

        // (5 cont.) ...and carry the right values.
        // (6) DIAGNOSABLE — each assertion says what was expected vs. actual.
        assertEquals(response.message(), "Your registration completed",
                "registration confirmation message did not match");
        assertEquals(response.email(), user.email(),
                "response should echo the e-mail that was registered");

        // (7) ISOLATED & FAST — no shared state, no network; the mock resets before each test.
    }

    @Test(description = "POST /register with an existing e-mail is rejected with 409")
    public void register_withExistingEmail_returns409Conflict() {
        // Same recipe, negative path.
        RegistrationRequest request = RegistrationRequest.forEmail(ApiConfig.EXISTING_EMAIL);

        ErrorResponse error =
                given().spec(rest)
                        .body(request)
                .when()
                        .post(ApiConfig.REGISTER)
                .then()
                        .statusCode(409)
                        .extract().as(ErrorResponse.class);

        assertEquals(error.message(), "The specified email already exists",
                "duplicate-e-mail registration should be rejected with the documented message");
    }
}
