package com.apiAutomation.client;

import com.apiAutomation.util.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    private final HttpClient client;
    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
    }

    public <T> ApiResponse<T> send(
            String endpoint,
            String method,
            Object requestBody,
            Class<T> responseType
    ) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json");

        if ("GET".equalsIgnoreCase(method)) {
            builder.GET();
        } else if ("POST".equalsIgnoreCase(method)) {
            String json = buildJsonRequestBody(requestBody);
            builder.POST(HttpRequest.BodyPublishers.ofString(json));
            logger.info("REQUEST {} {} bodyLength={}", method.toUpperCase(), baseUrl + endpoint, json.length());
        } else if ("PUT".equalsIgnoreCase(method)) {
            String json = buildJsonRequestBody(requestBody);
            builder.PUT(HttpRequest.BodyPublishers.ofString(json));
            logger.info("REQUEST {} {} bodyLength={}", method.toUpperCase(), baseUrl + endpoint, json.length());
        } else if ("DELETE".equalsIgnoreCase(method)) {
            builder.DELETE();
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            logger.info("REQUEST {} {}", method.toUpperCase(), baseUrl + endpoint);
        }

        HttpResponse<String> response =
                client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        logger.info("RESPONSE status={} contentType={}",
                response.statusCode(),
                response.headers().firstValue("content-type").orElse("n/a"));

        T body = null;
        if (responseType != Void.class && response.body() != null && !response.body().isBlank()) {
            if (responseType == String.class) {
                body = responseType.cast(response.body());
            } else if (responseType == Map.class) {
                body = responseType.cast(JsonParser.parseObject(response.body()));
            } else {
                throw new IllegalArgumentException(
                        "Unsupported response type without JSON library: " + responseType.getSimpleName()
                );
            }
        }

        return new ApiResponse<>(response.statusCode(), body, response.headers());
    }

    private String buildJsonRequestBody(Object requestBody) {
        if (requestBody == null) {
            return "{}";
        }
        if (requestBody instanceof String jsonString) {
            return jsonString;
        }
        if (requestBody instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> castedMap = (Map<String, ?>) map;
            return JsonParser.toJson(castedMap);
        }

        throw new IllegalArgumentException(
                "Unsupported request body type without JSON library: " + requestBody.getClass().getSimpleName()
        );
    }
}
