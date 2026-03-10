package com.apiAutomation.client;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TestLogger {

    private static final Logger logger = LoggerFactory.getLogger(TestLogger.class);

    private TestLogger() {
    }

    public static void step(String message) {
        logger.info(message);
        Allure.addAttachment("step", message);
    }

    public static void attach(String name, String content) {
        String formatted = formatContent(content);
        logger.info("{}:\n{}", name, formatted);
        Allure.addAttachment(name, formatted);
    }

    private static String formatContent(String content) {
        if (content == null) {
            return "null";
        }

        String trimmed = content.trim();
        if (looksLikeJson(trimmed)) {
            return prettyJson(trimmed);
        }
        if (looksLikeMapString(trimmed)) {
            return prettyMapString(trimmed);
        }
        if (looksLikeUserString(trimmed)) {
            return prettyUserString(trimmed);
        }
        return content;
    }

    private static boolean looksLikeJson(String content) {
        return (content.startsWith("{") && content.endsWith("}"))
                || (content.startsWith("[") && content.endsWith("]"));
    }

    private static boolean looksLikeMapString(String content) {
        return content.startsWith("{") && content.endsWith("}") && content.contains("=");
    }

    private static boolean looksLikeUserString(String content) {
        return content.startsWith("User{") && content.endsWith("}");
    }

    private static String prettyMapString(String content) {
        String inner = content.substring(1, content.length() - 1).trim();
        if (inner.isEmpty()) {
            return "{}";
        }

        String[] pairs = inner.split(",\\s*");
        StringBuilder builder = new StringBuilder("{\n");
        for (int i = 0; i < pairs.length; i++) {
            builder.append("  ").append(pairs[i].trim());
            if (i < pairs.length - 1) {
                builder.append(',');
            }
            builder.append('\n');
        }
        builder.append('}');
        return builder.toString();
    }

    private static String prettyUserString(String content) {
        String inner = content.substring("User{".length(), content.length() - 1).trim();
        if (inner.isEmpty()) {
            return "User{}";
        }

        String[] parts = inner.split(",\\s*");
        StringBuilder builder = new StringBuilder("User{\n");
        for (int i = 0; i < parts.length; i++) {
            builder.append("  ").append(parts[i].trim());
            if (i < parts.length - 1) {
                builder.append(',');
            }
            builder.append('\n');
        }
        builder.append('}');
        return builder.toString();
    }

    private static String prettyJson(String json) {
        if ("{}".equals(json) || "[]".equals(json)) {
            return json;
        }

        StringBuilder out = new StringBuilder();
        int indent = 0;
        boolean inQuotes = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == '{' || c == '[') {
                    out.append(c).append('\n');
                    indent++;
                    appendIndent(out, indent);
                    continue;
                }
                if (c == '}' || c == ']') {
                    out.append('\n');
                    indent = Math.max(0, indent - 1);
                    appendIndent(out, indent);
                    out.append(c);
                    continue;
                }
                if (c == ',') {
                    out.append(c).append('\n');
                    appendIndent(out, indent);
                    continue;
                }
                if (c == ':') {
                    out.append(": ");
                    continue;
                }
            }

            out.append(c);
        }

        return out.toString();
    }

    private static void appendIndent(StringBuilder out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.append("  ");
        }
    }
}
