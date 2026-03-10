package com.apiAutomation.client;

import com.apiAutomation.model.User;
import com.apiAutomation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiClientTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void getUserById() throws Exception {
        TestLogger.step("GET /users/1");
        ApiResponse<User> response = userService.getUserById(1);
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 200);
        ApiAssertions.assertHeaderContains(response, "content-type", "application/json");
        assertUserBasics(response.body());
    }

    @Test
    void createUser() throws Exception {
        TestLogger.step("POST /users");
        User payload = buildUser("John Doe", "jdoe", "john@example.com");

        ApiResponse<User> response = userService.createUser(payload);
        TestLogger.attach("request.user", String.valueOf(payload));
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 201);
        assertUserBasics(response.body());
    }

    @Test
    void getMissingUser() throws Exception {
        TestLogger.step("GET /users/999999");
        ApiResponse<User> response = userService.getUserById(999999);
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 404);
    }

    @Test
    void getInvalidEndpoint() throws Exception {
        TestLogger.step("GET /invalidEndpoint");
        ApiResponse<String> response = userService.getInvalidEndpoint("/invalidEndpoint");
        TestLogger.attach("response.body", response.body());

        ApiAssertions.assertStatusCode(response, 404);
    }

    @Test
    void updateUser() throws Exception {
        TestLogger.step("PUT /users/1");
        User payload = buildUser("Jane Doe", "jane", "jane@example.com");

        ApiResponse<User> response = userService.updateUser(1, payload);
        TestLogger.attach("request.user", String.valueOf(payload));
        TestLogger.attach("response.user", String.valueOf(response.body()));

        ApiAssertions.assertStatusCode(response, 200);
        assertUserBasics(response.body());
    }

    @Test
    void deleteUser() throws Exception {
        TestLogger.step("DELETE /users/1");
        ApiResponse<Void> response = userService.deleteUser(1);

        ApiAssertions.assertStatusCodeIn(response, 200, 204);
    }

    private User buildUser(String name, String username, String email) {
        User user = new User();
        user.name = name;
        user.username = username;
        user.email = email;
        user.phone = "1-770-736-8031 x56442";
        user.website = "example.com";
        return user;
    }

    private void assertUserBasics(User user) {
        if (user == null) {
            throw new AssertionError("Expected user but got null");
        }
        if (user.name == null || user.name.isBlank()) {
            throw new AssertionError("Expected name");
        }
        if (user.username == null || user.username.isBlank()) {
            throw new AssertionError("Expected username");
        }
        if (user.email == null || user.email.isBlank()) {
            throw new AssertionError("Expected email");
        }
    }
}
