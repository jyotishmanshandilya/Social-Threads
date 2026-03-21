package com.JobSwipe.webApp.util;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JsonUtilsTest {

    private JsonUtils jsonUtils;

    @BeforeEach
    void setUp() {
        jsonUtils = new JsonUtils();
    }

    // --- getFieldValue: flat field ---

    @Test
    void getFieldValue_returnsValueForExistingFlatField() {
        JSONObject json = new JSONObject("{\"name\": \"stripe\"}");
        assertThat(jsonUtils.getFieldValue(json, "name")).isEqualTo("stripe");
    }

    @Test
    void getFieldValue_returnsNullForMissingFlatField() {
        JSONObject json = new JSONObject("{}");
        assertThat(jsonUtils.getFieldValue(json, "name")).isNull();
    }

    // --- getFieldValue: nested dot-notation ---

    @Test
    void getFieldValue_returnsNestedValue() {
        JSONObject json = new JSONObject("{\"location\": {\"name\": \"Bengaluru\"}}");
        assertThat(jsonUtils.getFieldValue(json, "location.name")).isEqualTo("Bengaluru");
    }

    @Test
    void getFieldValue_returnsNullWhenNestedParentMissing() {
        JSONObject json = new JSONObject("{}");
        assertThat(jsonUtils.getFieldValue(json, "location.name")).isNull();
    }

    @Test
    void getFieldValue_returnsNullWhenNestedChildMissing() {
        JSONObject json = new JSONObject("{\"location\": {}}");
        assertThat(jsonUtils.getFieldValue(json, "location.name")).isNull();
    }

    @Test
    void getFieldValue_handlesDoublyNestedPath() {
        JSONObject json = new JSONObject("{\"a\": {\"b\": {\"c\": \"deep\"}}}");
        assertThat(jsonUtils.getFieldValue(json, "a.b.c")).isEqualTo("deep");
    }

    @Test
    void getFieldValue_returnsNullWhenNestedParentIsNotObject() {
        JSONObject json = new JSONObject("{\"location\": \"string-not-object\"}");
        assertThat(jsonUtils.getFieldValue(json, "location.name")).isNull();
    }
}
