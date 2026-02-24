package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.CompanyJobs;
import com.JobSwipe.webApp.model.CompanySeedIdDTO;
import com.JobSwipe.webApp.repository.CompanyJobsRepository;
import com.JobSwipe.webApp.repository.SeedListRepository;
import com.JobSwipe.webApp.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAggregatorService {

    private final SeedListRepository seedListRepository;
    private final CompanyJobsRepository companyJobsRepository;
    private final JobDetailExtractionService jobDetailExtractionService;
    private final JsonUtils jsonUtils;

    private static final int BATCH_SIZE = 100;

    public void importJobs() {
        List<CompanySeedIdDTO> companySlugs = seedListRepository.findDistinctCompany();

        for (CompanySeedIdDTO companySeedIdDTO : companySlugs) {
            String company = companySeedIdDTO.getCompany();
            UUID seedListId = companySeedIdDTO.getId();
            String jobsApiUrl = String.format("https://boards-api.greenhouse.io/v1/boards/%s/jobs?content=true", company);
            try {
                JSONObject result = jsonUtils.fetchJson(jobsApiUrl);
                JSONArray jobsArray = result.optJSONArray("jobs");
                if (jobsArray == null) {
                    log.info("No jobs found for company: {}", company);
                    continue;
                }

                log.info("Found {} jobs for company: {}", jobsArray.length(), company);

                int countNewJobs = 0;
                List<CompanyJobs> jobsToSave = new ArrayList<>(BATCH_SIZE);
                for (int i = 0; i < jobsArray.length(); i++) {
                    JSONObject job = jobsArray.getJSONObject(i);

                    String jobId = jsonUtils.getFieldValue(job, "id");

                    // if job already exists in the DB, skip to next job
                    if (companyJobsRepository.existsByJobId(jobId)) {
                        log.info("Job with jobId={} already exists.", jobId);
                        continue;
                    }

                    String internalJobId = jsonUtils.getFieldValue(job, "internal_job_id");
                    String jobTitle = jsonUtils.getFieldValue(job, "title");
                    String applicationUrl = jsonUtils.getFieldValue(job, "absolute_url");
                    String location = jsonUtils.getFieldValue(job, "location.name");
                    // TODO: need to look into mapping workplace type properly
                    String workplaceType = jsonUtils.getFieldValue(job, "workplace_type");
                    String language = jsonUtils.getFieldValue(job, "language");
                    String postedDateString = jsonUtils.getFieldValue(job, "updated_at");

                    LocalDateTime postedDate = null;
                    if (postedDateString != null && !postedDateString.isEmpty()) {
                        try {
                            ZonedDateTime zdt = ZonedDateTime.parse(postedDateString, DateTimeFormatter.ISO_DATE_TIME);
                            postedDate = zdt.toLocalDateTime();
                        } catch (Exception e) {
                            log.warn("Could not parse posted date '{}' for jobId={}, company={}: {}", postedDateString, jobId, company, e.getMessage());
                        }
                    }

                    Integer yearsOfExperience = jobDetailExtractionService.extractYearsOfExperience(job.optString("content", null));

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

                    // Batch save every 100 jobs
                    if (i % BATCH_SIZE == 0) {
                        companyJobsRepository.saveAll(jobsToSave);
                        jobsToSave.clear();
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


}