package com.demoshop.api.tests;

import com.demoshop.api.base.BaseApiTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * SKELETON — fill in following {@code API_TEST_STANDARD.md}; {@code RegistrationApiTest}
 * is the worked example to copy. The mock backend already serves
 * {@code GET /api/catalog/digital-downloads} from
 * {@code mockdata/digital-downloads-catalog.json}.
 */
public class DigitalDownloadsApiTest extends BaseApiTest {

    @Test(description = "GET /catalog/digital-downloads returns the published product list")
    public void getDigitalDownloads_returnsExpectedProducts() {
        // (1) NAME      done above: endpoint_condition_expectedResult.
        // (2) ARRANGE   nothing to build — this is a GET; the mock is already stubbed.
        // (3) ACT       given().spec(rest).get(ApiConfig.CATALOG_DIGITAL_DOWNLOADS)
        // (4) STATUS    expect 200.
        // (5) BODY      extract().as(Catalog.class); assert the category and that the
        //               product list is non-empty and contains a known product.
        // (6) MESSAGES  give every assertion an expected-vs-actual message.
        // (7) ISOLATED  no other-test dependency; the mock resets before each test.
        throw new SkipException(
                "Skeleton — implement per API_TEST_STANDARD.md; see RegistrationApiTest.");
    }
}
