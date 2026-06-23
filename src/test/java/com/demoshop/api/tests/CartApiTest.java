package com.demoshop.api.tests;

import com.demoshop.api.base.BaseApiTest;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * SKELETON — add-to-cart at the API level. The mock backend serves
 * {@code POST /api/cart/items} and {@code GET /api/cart} from
 * {@code mockdata/cart-with-item.json}. Copy the shape of {@code RegistrationApiTest}.
 */
public class CartApiTest extends BaseApiTest {

    @Test(description = "POST /cart/items adds a product and the cart then contains it")
    public void addItemToCart_thenCartContainsProduct() {
        // (1) NAME      done above.
        // (2) ARRANGE   build a CartItem request body (productId + quantity).
        // (3) ACT       POST it to ApiConfig.CART_ITEMS.
        // (4) STATUS    expect 201.
        // (5) BODY      extract().as(Cart.class); assert totalItems and that items
        //               contains the productId that was added.
        // (6) MESSAGES  expected-vs-actual on every assertion.
        // (7) ISOLATED  fresh mock per test.
        throw new SkipException(
                "Skeleton — implement per API_TEST_STANDARD.md; see RegistrationApiTest.");
    }
}
