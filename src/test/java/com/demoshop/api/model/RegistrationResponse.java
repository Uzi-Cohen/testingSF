package com.demoshop.api.model;

/** Success body for {@code POST /api/customer/register}. */
public record RegistrationResponse(long customerId, String email, String message) {
}
