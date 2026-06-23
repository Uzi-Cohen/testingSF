package com.demoshop.api.model;

import java.util.List;

/** The shopping cart: its line items and the roll-up totals. */
public record Cart(List<CartItem> items, int totalItems, double subTotal) {
}
