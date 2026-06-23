package com.demoshop.api.model;

/** Standard error body the backend returns for a rejected request. */
public record ErrorResponse(String error, String message) {
}
