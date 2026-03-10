package com.apiAutomation.util;

import com.apiAutomation.client.TestLogger;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserTest {

    @Test
    void parseObject_parsesFlatValues() {
        TestLogger.step("Parse flat JSON values");
        String json = "{\"id\":1,\"title\":\"hello\",\"completed\":false}";
        TestLogger.attach("input.json", json);

        Map<String, String> parsed = JsonParser.parseObject(json);
        TestLogger.attach("parsed.map", JsonParser.toJson(parsed));

        assertEquals("1", parsed.get("id"));
        assertEquals("hello", parsed.get("title"));
        assertEquals("false", parsed.get("completed"));
    }

    @Test
    void parseObject_skipsNestedValues() {
        TestLogger.step("Parse JSON and skip nested objects");
        String json = "{\"id\":1,\"name\":\"Leanne\",\"address\":{\"city\":\"Gwenborough\"}}";
        TestLogger.attach("input.json", json);

        Map<String, String> parsed = JsonParser.parseObject(json);
        TestLogger.attach("parsed.map", JsonParser.toJson(parsed));

        assertEquals("1", parsed.get("id"));
        assertEquals("Leanne", parsed.get("name"));
    }

    @Test
    void toJson_serializesMap() {
        TestLogger.step("Serialize map to JSON");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("title", "foo");
        payload.put("userId", 1);
        payload.put("published", true);
        TestLogger.attach("input.map", JsonParser.toJson(payload));

        String json = JsonParser.toJson(payload);
        TestLogger.attach("output.json", json);

        assertEquals("{\"title\":\"foo\",\"userId\":1,\"published\":true}", json);
    }

    @Test
    void parseObject_rejectsInvalidInput() {
        TestLogger.step("Reject invalid JSON input");
        assertThrows(IllegalArgumentException.class, () -> JsonParser.parseObject(""));
        assertThrows(IllegalArgumentException.class, () -> JsonParser.parseObject("[]"));
    }
}
