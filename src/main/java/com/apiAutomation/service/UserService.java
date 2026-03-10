package com.apiAutomation.service;

import com.apiAutomation.client.ApiClient;
import com.apiAutomation.client.ApiResponse;
import com.apiAutomation.config.Config;
import com.apiAutomation.model.User;
import com.apiAutomation.util.JsonParser;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserService {

    private final ApiClient apiClient;

    public UserService() {
        this.apiClient = new ApiClient(Config.getBaseUrl());
    }

    public ApiResponse<User> getUserById(int id) throws Exception {
        ApiResponse<String> rawResponse = apiClient.send(
                "/users/" + id,
                "GET",
                null,
                String.class
        );

        User user = parseUser(rawResponse.body());

        return new ApiResponse<>(rawResponse.statusCode(), user, rawResponse.headers());
    }

    public ApiResponse<User> createUser(User user) throws Exception {
        ApiResponse<String> rawResponse = apiClient.send(
                "/users",
                "POST",
                toPayload(user),
                String.class
        );

        return new ApiResponse<>(rawResponse.statusCode(), parseUser(rawResponse.body()), rawResponse.headers());
    }

    public ApiResponse<User> updateUser(int id, User user) throws Exception {
        ApiResponse<String> rawResponse = apiClient.send(
                "/users/" + id,
                "PUT",
                toPayload(user),
                String.class
        );

        return new ApiResponse<>(rawResponse.statusCode(), parseUser(rawResponse.body()), rawResponse.headers());
    }

    public ApiResponse<Void> deleteUser(int id) throws Exception {
        return apiClient.send(
                "/users/" + id,
                "DELETE",
                null,
                Void.class
        );
    }

    public ApiResponse<String> getInvalidEndpoint(String endpoint) throws Exception {
        return apiClient.send(
                endpoint,
                "GET",
                null,
                String.class
        );
    }

    private User parseUser(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        Map<String, String> parsed = JsonParser.parseObject(json);
        if (parsed.isEmpty()) {
            return null;
        }

        User user = new User();
        user.id = parseIntSafe(parsed.get("id"));
        user.name = parsed.get("name");
        user.username = parsed.get("username");
        user.email = parsed.get("email");
        user.phone = parsed.get("phone");
        user.website = parsed.get("website");
        return user;
    }

    private int parseIntSafe(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private Map<String, Object> toPayload(User user) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (user == null) {
            return payload;
        }

        if (user.name != null) {
            payload.put("name", user.name);
        }
        if (user.username != null) {
            payload.put("username", user.username);
        }
        if (user.email != null) {
            payload.put("email", user.email);
        }
        if (user.phone != null) {
            payload.put("phone", user.phone);
        }
        if (user.website != null) {
            payload.put("website", user.website);
        }

        return payload;
    }
}
