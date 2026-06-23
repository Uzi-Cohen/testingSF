package com.demoshop.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Test-data model for a registering customer. {@link #randomUser()} is a small
 * factory that guarantees a unique e-mail per run, so registration tests never
 * collide on "The specified email already exists".
 *
 * @param firstName customer first name
 * @param lastName  customer last name
 * @param email     unique, throwaway e-mail address
 * @param password  throwaway password — test data, never a real credential
 */
public record User(String firstName,
                   String lastName,
                   String email,
                   String password) {

    public static User randomUser() {
        long timestamp = System.currentTimeMillis();
        int salt = ThreadLocalRandom.current().nextInt(1000, 9999);
        // example.com is reserved for documentation/testing (RFC 2606).
        String email = "test.automation.%d%d@example.com".formatted(timestamp, salt);
        return new User("Test", "Automation", email, "Test@12345");
    }
}
