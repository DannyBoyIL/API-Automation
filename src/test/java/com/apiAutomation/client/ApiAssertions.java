package com.apiAutomation.client;

import org.junit.jupiter.api.Assertions;

public final class ApiAssertions {

    private ApiAssertions() {
    }

    public static void assertStatusCode(ApiResponse<?> response, int expected) {
        if (response.statusCode() != expected) {
            Assertions.fail("Expected status " + expected + " but was " + response.statusCode());
        }
    }

    public static void assertStatusCodeIn(ApiResponse<?> response, int... expectedValues) {
        for (int value : expectedValues) {
            if (response.statusCode() == value) {
                return;
            }
        }
        Assertions.fail("Unexpected status code: " + response.statusCode());
    }

    public static void assertHeaderContains(ApiResponse<?> response, String headerName, String expectedValue) {
        String actual = response.headers().firstValue(headerName).orElse("");
        if (!actual.toLowerCase().contains(expectedValue.toLowerCase())) {
            Assertions.fail(
                    "Expected header '" + headerName + "' to contain '" + expectedValue + "' but was '" + actual + "'"
            );
        }
    }

    public static void assertBodyContains(String body, String expectedFragment) {
        String normalizedBody = body == null ? null : body.replaceAll("\\s+", "");
        String normalizedExpected = expectedFragment.replaceAll("\\s+", "");
        if (normalizedBody == null || !normalizedBody.contains(normalizedExpected)) {
            Assertions.fail("Expected response body to contain: " + expectedFragment + " but was: " + body);
        }
    }
}
