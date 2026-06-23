package com.demoshop.api.tests;

import com.demoshop.api.base.BaseApiTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * SKELETON — a backend <em>contract</em> test, distinct from the functional tests:
 * it asserts the catalogue response always matches the published JSON schema
 * ({@code schema/catalog.schema.json}) and content-type, guarding the API's contract
 * with its consumers rather than specific data values.
 */
public class CatalogContractTest extends BaseApiTest {

    @Test(description = "GET /catalog/digital-downloads honours the published JSON schema")
    public void digitalDownloads_matchesPublishedSchema() {
        // (1) NAME      done above.
        // (2) ARRANGE   none — GET against the already-stubbed mock.
        // (3) ACT       given().spec(rest).get(ApiConfig.CATALOG_DIGITAL_DOWNLOADS)
        // (4) STATUS    expect 200 and Content-Type application/json.
        // (5) BODY      .body(matchesJsonSchemaInClasspath("schema/catalog.schema.json"))
        // (6) MESSAGES  schema mismatches are self-describing; add context if asserting fields.
        // (7) ISOLATED  fresh mock per test.
        throw new SkipException(
                "Skeleton — implement per API_TEST_STANDARD.md; see RegistrationApiTest.");
    }
}
