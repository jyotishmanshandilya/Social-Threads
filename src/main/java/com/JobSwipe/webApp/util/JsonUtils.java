package com.JobSwipe.webApp.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {

    public JSONObject fetchJson(String urlStr) throws java.io.IOException {
        log.debug("Fetching from URL: {}", urlStr);

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();

        if (status == 400) {
            log.warn("HTTP 400 from {}", urlStr);
            conn.disconnect();
            return new JSONObject();
        }

        try (InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream()) {
            if (is == null) {
                conn.disconnect();
                throw new java.io.IOException("No response body (status=" + status + ")");
            }
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (status >= 200 && status < 300) {
                return new JSONObject(body);
            }
            throw new java.io.IOException("HTTP " + status + " from " + urlStr + " body: " + body);
        } finally {
            conn.disconnect();
        }
    }

    public String getFieldValue(JSONObject jsonObject, String fieldName) {
        if (fieldName.contains(".")) {
            String[] parts = fieldName.split("\\.", 2);
            JSONObject nested = jsonObject.optJSONObject(parts[0]);
            if (nested == null) return null;
            return getFieldValue(nested, parts[1]);
        }
        return jsonObject.has(fieldName) ? jsonObject.optString(fieldName, null) : null;
    }
}
