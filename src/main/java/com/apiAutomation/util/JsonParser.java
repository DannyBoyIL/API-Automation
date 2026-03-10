package com.apiAutomation.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonParser {

    private JsonParser() {
    }

    public static Map<String, String> parseObject(String json) {
        if (json == null) {
            throw new IllegalArgumentException("JSON input cannot be null");
        }

        String trimmed = json.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("JSON input cannot be empty");
        }
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Only flat JSON objects are supported");
        }

        String content = trimmed.substring(1, trimmed.length() - 1).trim();
        Map<String, String> result = new LinkedHashMap<>();
        if (content.isEmpty()) {
            return result;
        }

        for (String pair : splitTopLevel(content, ',')) {
            List<String> keyValue = splitFirstTopLevel(pair, ':');
            if (keyValue.size() != 2) {
                throw new IllegalArgumentException("Invalid JSON key/value pair: " + pair);
            }

            String key = unquote(keyValue.get(0).trim());
            String value = keyValue.get(1).trim();

            if (value.startsWith("{") || value.startsWith("[")) {
                // Intentionally skip nested values in this minimal parser.
                continue;
            }

            result.put(key, normalizeValue(value));
        }

        return result;
    }

    public static String toJson(Map<String, ?> data) {
        if (data == null || data.isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ?> entry : data.entrySet()) {
            if (!first) {
                json.append(',');
            }
            first = false;

            json.append('"').append(escape(entry.getKey())).append('"').append(':');
            Object value = entry.getValue();

            if (value == null) {
                json.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append('"').append(escape(String.valueOf(value))).append('"');
            }
        }
        json.append('}');

        return json.toString();
    }

    public static int getInt(Map<String, String> map, String key) {
        return Integer.parseInt(requireKey(map, key));
    }

    public static boolean getBoolean(Map<String, String> map, String key) {
        return Boolean.parseBoolean(requireKey(map, key));
    }

    public static String getString(Map<String, String> map, String key) {
        return requireKey(map, key);
    }

    private static String requireKey(Map<String, String> map, String key) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException("Missing key: " + key);
        }
        return map.get(key);
    }

    private static String normalizeValue(String value) {
        String normalized = value.trim();
        if (normalized.startsWith("\"") && normalized.endsWith("\"")) {
            return unescape(normalized.substring(1, normalized.length() - 1));
        }
        return normalized;
    }

    private static String unquote(String input) {
        String trimmed = input.trim();
        if (!(trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            throw new IllegalArgumentException("JSON keys must be quoted: " + input);
        }
        return unescape(trimmed.substring(1, trimmed.length() - 1));
    }

    private static String escape(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private static String unescape(String input) {
        return input
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private static List<String> splitTopLevel(String input, char delimiter) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int objectDepth = 0;
        int arrayDepth = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '"' && (i == 0 || input.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            if (!inQuotes) {
                if (c == '{') {
                    objectDepth++;
                } else if (c == '}') {
                    objectDepth--;
                } else if (c == '[') {
                    arrayDepth++;
                } else if (c == ']') {
                    arrayDepth--;
                }
            }

            if (c == delimiter && !inQuotes && objectDepth == 0 && arrayDepth == 0) {
                parts.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        parts.add(current.toString().trim());
        return parts;
    }

    private static List<String> splitFirstTopLevel(String input, char delimiter) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int objectDepth = 0;
        int arrayDepth = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '"' && (i == 0 || input.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            if (!inQuotes) {
                if (c == '{') {
                    objectDepth++;
                } else if (c == '}') {
                    objectDepth--;
                } else if (c == '[') {
                    arrayDepth++;
                } else if (c == ']') {
                    arrayDepth--;
                }
            }

            if (c == delimiter && !inQuotes && objectDepth == 0 && arrayDepth == 0) {
                parts.add(current.toString());
                parts.add(input.substring(i + 1));
                return parts;
            }

            current.append(c);
        }

        parts.add(input);
        return parts;
    }
}
