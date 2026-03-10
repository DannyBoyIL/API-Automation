package com.apiAutomation.client;

import java.net.http.HttpHeaders;

public record ApiResponse<T>(int statusCode, T body, HttpHeaders headers) {}
