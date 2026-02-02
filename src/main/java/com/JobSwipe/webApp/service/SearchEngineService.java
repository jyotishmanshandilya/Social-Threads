package com.JobSwipe.webApp.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class SearchEngineService {
    @Value("${google.cx}")
    private String googleCx;

    @Value("${google.api-key}")
    private String googleApiKey;

    public JSONObject googleSearch(String query, int start) throws IOException, JSONException {
        String apiUrl = "https://www.googleapis.com/customsearch/v1";
        String url = String.format("%s?key=%s&cx=%s&q=%s&start=%d",
                apiUrl, googleApiKey, googleCx,
                URLEncoder.encode(query, StandardCharsets.UTF_8), start);

        String response = httpGet(url);
        return new JSONObject(response);
    }

    public String httpGet(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("Accept", "application/json");
        try (InputStream is = conn.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
