package com.JobSwipe.webApp.service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.*;

import com.JobSwipe.webApp.entities.SeedList;
import com.JobSwipe.webApp.repository.SeedListRepository;
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

    public static final List<String> DISCOVERY_QUERIES = Arrays.asList(
            // National scope
            "site:boards.greenhouse.io software engineer India",
            "site:boards.greenhouse.io software developer India",
            "site:boards.greenhouse.io sde India",
            "site:boards.greenhouse.io backend engineer India",
            "site:boards.greenhouse.io frontend engineer India",
            "site:boards.greenhouse.io fullstack engineer India",
            "site:job-boards.greenhouse.io software engineer India",
            // Major cities (Bengaluru, Bangalore, Mumbai, Chennai, Pune, Hyderabad, Noida, Gurgaon, Delhi)
            "site:boards.greenhouse.io software engineer Bengaluru",
            "site:boards.greenhouse.io software engineer Bangalore",
            "site:boards.greenhouse.io software engineer Mumbai",
            "site:boards.greenhouse.io software engineer Chennai",
            "site:boards.greenhouse.io software engineer Pune",
            "site:boards.greenhouse.io software engineer Hyderabad",
            "site:boards.greenhouse.io software engineer Noida",
            "site:boards.greenhouse.io software engineer Gurgaon",
            "site:boards.greenhouse.io software engineer Delhi",
            // Role/general with remote
            "site:boards.greenhouse.io software engineer Remote India",
            // Synonyms for developer/engineering roles in all cities
            "site:boards.greenhouse.io software developer Bengaluru",
            "site:boards.greenhouse.io software developer Bangalore",
            "site:boards.greenhouse.io sde Hyderabad",
            "site:boards.greenhouse.io backend Pune",
            "site:boards.greenhouse.io frontend engineer Mumbai",
            "site:boards.greenhouse.io fullstack Chennai",
            // Catch-alls and industry-wide
            "site:boards.greenhouse.io engineer India",
            "site:boards.greenhouse.io India software",
            "site:job-boards.greenhouse.io India software engineer",
            "site:job-boards.greenhouse.io India software developer",
            "site:job-boards.greenhouse.io India sde"
    );

    public static final List<String> INDIA_KEYWORDS = Arrays.asList(
            "india", "bengaluru", "bangalore", "hyderabad",
            "pune", "chennai", "noida", "gurgaon", "remote india"
    );

    public static final long REQUEST_SLEEP_MS = 1000;

    public void discoverGreenhouseBoards() {
        if (googleApiKey.isEmpty() || googleCx.isEmpty()) {
            log.error("googleApiKey or googleCx not set");
        }

        for (String query : DISCOVERY_QUERIES) {
            log.info("Discovering greenhouse boards for query: " + query);

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

                    log.info("Found candidate board: {}", board);

                    JSONObject jobsData = null;
                    try {
                        jobsData = validateBoard(board);
                    } catch (IOException ignore) {}
                    if (jobsData == null) {
                        log.error("Invalid board");
                        continue;
                    }

                    if (!hasIndiaJobs(jobsData, INDIA_KEYWORDS)) {
                        log.info("No jobs found with location: India, for board: {}", board);
                        continue;
                    }

                    saveBoard(board);
                    log.info("Valid board with India jobs: {}", board);

                    try { Thread.sleep(REQUEST_SLEEP_MS); } catch (InterruptedException ignore) {}
                }
                try { Thread.sleep(REQUEST_SLEEP_MS); } catch (InterruptedException ignore) {}
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
                String locName = loc.optString("name", "").toLowerCase();
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
