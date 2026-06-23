package com.demoshop.api.model;

import java.util.List;

/** A product category and its products (e.g. the Digital Downloads catalogue). */
public record Catalog(String category, List<Product> products) {
}
