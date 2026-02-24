package com.JobSwipe.webApp.service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

import com.JobSwipe.webApp.entities.SeedList;
import com.JobSwipe.webApp.repository.SeedListRepository;
import com.JobSwipe.webApp.util.BackoffUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedAggregatorService {
    @Value("${google.cx}")
    private String googleCx;

    @Value("${google.api-key}")
    private String googleApiKey;

    private final SearchEngineService searchEngineService;
    private final SeedListRepository seedListRepository;
    private final QueryGeneratorService queryGeneratorService;
    private final BackoffUtils backoffUtils;

    public static final long REQUEST_SLEEP_MS = 1000;

    public void discoverGreenhouseBoards() {
        if (googleApiKey == null || googleApiKey.isEmpty() ||
                googleCx == null || googleCx.isEmpty()) {
            log.error("googleApiKey or googleCx not set");
            return;
        }

        // Build queries dynamically from config
        List<String> discoveryQueries = queryGeneratorService.buildQueries();
        // Derive India keywords from the same locations config
        List<String> indiaKeywords = queryGeneratorService.indiaKeywords();


        for (String query : discoveryQueries) {
            log.info("Discovering greenhouse boards for query: {}", query);

            for (int start = 1; start < 10000; start += 10) {
                JSONObject result;
                try {
                    result = searchEngineService.googleSearch(query, start);
                } catch (Exception e) {
                    log.error("Google search failed", e);
                    break;
                }

                JSONArray items = result.optJSONArray("items");
                if (items == null || items.isEmpty()) {
                    break;
                }

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String link = item.optString("link", "");
                    String board = extractGreenhouseBoard(link);

                    if (board == null || doesBoardExist(board)) {
                        continue;
                    }

                    JSONObject jobsData = null;
                    try {
                        jobsData = validateBoard(board);
                    } catch (IOException ignore) {}

                    if (jobsData == null) {
                        continue;
                    }

                    if (!hasIndiaJobs(jobsData, indiaKeywords)) {
                        log.info("No jobs found with location: India, for board: {}", board);
                        continue;
                    }

                    saveBoard(board);

                    backoffUtils.sleepQuietly(REQUEST_SLEEP_MS);
                }
                backoffUtils.sleepQuietly(REQUEST_SLEEP_MS);
            }
        }
    }

    public JSONObject validateBoard(String board) throws IOException {
        String apiURL = String.format("https://boards-api.greenhouse.io/v1/boards/%s/jobs", board);
        try {
            String response = searchEngineService.httpGet(apiURL);
            return new JSONObject(response);
        } catch (IOException e) {
            return null;
        }
    }

    public String extractGreenhouseBoard(String url) {
        Pattern p = Pattern.compile("greenhouse\\.io/([^/]+)");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public boolean hasIndiaJobs(JSONObject jobsData, List<String> indiaKeywords) {
        if (jobsData == null) return false;
        JSONArray jobs = jobsData.optJSONArray("jobs");
        if (jobs == null) return false;

        for (int i = 0; i < jobs.length(); i++) {
            JSONObject job = jobs.getJSONObject(i);
            JSONObject loc = job.optJSONObject("location");
            if (loc != null) {
                String locName = loc.optString("name", "").toLowerCase(Locale.ROOT);
                for (String k : indiaKeywords) {
                    if (locName.contains(k)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean doesBoardExist(String board) {
        String normalizedBoard = board.trim().toLowerCase(Locale.ROOT);
        return seedListRepository.existsByCompany(normalizedBoard);
    }

    public void saveBoard(String board) {
        SeedList seedListEntry = SeedList.builder()
                .id(UUID.randomUUID())
                .company(board.trim().toLowerCase(Locale.ROOT))
                .jobBoard("greenhouse_board")
                .validationStatus(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        seedListRepository.save(seedListEntry);
    }
}