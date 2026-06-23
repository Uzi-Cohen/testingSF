package com.demoshop.api.client;

/**
 * Endpoint paths and shared constants for the API/backend suite — one source of
 * truth referenced by both the mock backend and the tests, so a path is never
 * spelled out twice. Mirrors the role {@code config.Config} plays for the UI suite.
 */
public final class ApiConfig {

    private ApiConfig() {
        // Constants holder — not instantiable.
    }

    public static final String REGISTER = "/api/customer/register";
    public static final String CATALOG_DIGITAL_DOWNLOADS = "/api/catalog/digital-downloads";
    public static final String CART_ITEMS = "/api/cart/items";
    public static final String CART = "/api/cart";

    /** An e-mail the backend already knows — used to exercise the duplicate-e-mail path. */
    public static final String EXISTING_EMAIL = "existing.customer@example.com";
}
