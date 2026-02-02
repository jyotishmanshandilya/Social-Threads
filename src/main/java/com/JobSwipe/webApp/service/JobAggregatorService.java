package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.CompanyJobs;
import com.JobSwipe.webApp.model.CompanySeedIdDTO;
import com.JobSwipe.webApp.repository.CompanyJobsRepository;
import com.JobSwipe.webApp.repository.SeedListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAggregatorService {

    private final SeedListRepository seedListRepository;
    private final CompanyJobsRepository companyJobsRepository;
    private final JobDetailExtractionService jobDetailExtractionService;

    public void importJobs() {
        List<CompanySeedIdDTO> companySlugs = seedListRepository.findDistinctCompany();

        for (CompanySeedIdDTO companySeedIdDTO : companySlugs) {
            String company = companySeedIdDTO.getCompany();
            UUID seedListId = companySeedIdDTO.getId();
            String jobsApiUrl = String.format("https://boards-api.greenhouse.io/v1/boards/%s/jobs?content=true", company);
            try {
                JSONObject result = fetchJobsJson(jobsApiUrl);
                JSONArray jobsArray = result.optJSONArray("jobs");
                if (jobsArray == null) {
                    log.info("No jobs found for company: {}", company);
                    continue;
                }

                log.info("Found {} jobs for company: {}", jobsArray.length(), company);

                int countNewJobs = 0;
                List<CompanyJobs> jobsToSave = new ArrayList<>();
                for (int i = 0; i < jobsArray.length(); i++) {
                    JSONObject job = jobsArray.getJSONObject(i);

                    String jobId = job.has("id") ? String.valueOf(job.get("id")) : null;

                    // if job already exists in the DB, skip to next job
                    if (companyJobsRepository.existsByJobId(jobId)) {
                        log.info("Job with jobId={} already exists. Skipping.", jobId);
                        continue;
                    }

                    String internalJobId = job.has("internal_job_id") ? String.valueOf(job.get("internal_job_id")) : null;
                    String jobTitle = job.optString("title", null);
                    String applicationUrl = job.optString("absolute_url", null);
                    String location = job.optJSONObject("location") != null
                            ? job.getJSONObject("location").optString("name", "")
                            : "";
                    String postedDateString = job.optString("updated_at", null);
                    if (postedDateString == null) {
                        postedDateString = job.optString("first_published", null);
                    }
                    LocalDateTime postedDate = null;
                    if (postedDateString != null && !postedDateString.isEmpty()) {
                        try {
                            ZonedDateTime zdt = ZonedDateTime.parse(postedDateString, DateTimeFormatter.ISO_DATE_TIME);
                            postedDate = zdt.toLocalDateTime();
                        } catch (Exception e) {
                            log.warn("Could not parse posted date '{}' for jobId={}, company={}: {}", postedDateString, jobId, company, e.getMessage());
                        }
                    }

                    // TODO: need to look into mapping workplace type properly
                    String workplaceType = job.optString("workplace_type", null);

                    // Language field
                    String language = job.optString("language", null);

                    // TODO: look into extracting years of experience from job description page
                    Integer yearsOfExperience = jobDetailExtractionService.extractYearsOfExperience(job.optString("content", null));
//                    String yearsOfExperienceStr = extractYearsOfExperience(applicationUrl);
//                    Integer yearsOfExperience = null;
//                    if (yearsOfExperienceStr != null && !yearsOfExperienceStr.isEmpty()) {
//                        try {
//                            yearsOfExperience = Integer.parseInt(yearsOfExperienceStr);
//                        } catch (NumberFormatException nfe) {
//                            log.warn("Couldn't parse years of experience '{}' for jobId={}, company={}", yearsOfExperienceStr, jobId, company);
//                        }
//                    }

                    jobsToSave.add(CompanyJobs.builder()
                            .id(UUID.randomUUID())
                            .seedListId(seedListId)
                            .jobTitle(jobTitle)
                            .location(location)
                            .applicationUrl(applicationUrl)
                            .jobId(jobId)
                            .internalJobId(internalJobId)
                            .workplaceType(workplaceType)
                            .postedDate(postedDate)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .yearsOfExperience(yearsOfExperience)
                            .language(language)
                            .build());
                    countNewJobs++;

                    // Batch save every 10 jobs
                    if (i % 10 == 0) {
                        companyJobsRepository.saveAll(jobsToSave);
                        jobsToSave.clear();
                        log.info("Saved 10 jobs for company: {}", company);
                    }
                    log.debug("Prepared job for saving: [{}] {} - {}", jobId, jobTitle, company);
                }

                // Save any remaining jobs
                if (!jobsToSave.isEmpty()) {
                    companyJobsRepository.saveAll(jobsToSave);
                }

                log.info("Saved {} jobs for company: {}", countNewJobs, company);

            } catch (Exception e) {
                log.error("Failed to fetch or save jobs for {}: {}", company, e.getMessage(), e);
            }
        }
    }

    /** Utility to fetch the jobs API as JSON */
    private JSONObject fetchJobsJson(String urlStr) throws java.io.IOException {
        log.debug("Fetching job data from URL: {}", urlStr);
        try (InputStream is = new java.net.URL(urlStr).openStream()) {
            String jsonText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(jsonText);
        }
    }

    /** Scrape the years of experience required from the job's HTML description */
    public String extractYearsOfExperience(String jobAbsoluteUrl) {
        if (jobAbsoluteUrl == null) {
            log.warn("Job absolute URL is null, cannot extract years of experience.");
            return null;
        }
        try {
            Document doc = Jsoup.connect(jobAbsoluteUrl)
                    .userAgent("Mozilla/5.0")
                    .get();

            // Try multiple selectors for robustness
            String description = doc.select(".main, .content, .job-description, .description, [data-qa=job-description]").text();

            Pattern pattern = Pattern.compile("(\\d+)[+]?\\s+years?['’]?(\\s+of)?\\s+experience", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(description);

            if (matcher.find()) {
                return matcher.group(1); // Returns just the number (e.g., "3")
            }
            return null; // Not found
        } catch (Exception e) {
            log.warn("Failed to scrape years of experience from {}: {}", jobAbsoluteUrl, e.getMessage());
            return null;
        }
    }
}