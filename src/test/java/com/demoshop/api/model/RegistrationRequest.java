package com.demoshop.api.model;

import com.demoshop.model.User;

/**
 * Request body for {@code POST /api/customer/register}. The factories build one from
 * the same {@link User} the UI suite uses, so test data is generated the same way
 * across layers — and serialised to JSON by Jackson (no hand-written JSON strings).
 */
public record RegistrationRequest(String firstName,
                                  String lastName,
                                  String email,
                                  String password,
                                  String confirmPassword) {

    /** Builds a request from a generated user (unique e-mail per run). */
    public static RegistrationRequest from(User user) {
        return new RegistrationRequest(user.firstName(), user.lastName(),
                user.email(), user.password(), user.password());
    }

    /** Builds a valid request for a specific e-mail (e.g. to exercise the duplicate path). */
    public static RegistrationRequest forEmail(String email) {
        return new RegistrationRequest("Test", "Automation", email, "Test@12345", "Test@12345");
    }
}
