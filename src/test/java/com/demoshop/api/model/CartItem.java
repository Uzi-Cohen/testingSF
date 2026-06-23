package com.demoshop.api.model;

/** A single line item in the shopping cart. */
public record CartItem(int productId, String productName, int quantity, double unitPrice) {
}
