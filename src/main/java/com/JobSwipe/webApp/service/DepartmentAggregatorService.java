package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.JobDepartmentMapping;
import com.JobSwipe.webApp.model.CompanySeedIdDTO;
import com.JobSwipe.webApp.repository.JobDepartmentMappingRepository;
import com.JobSwipe.webApp.repository.SeedListRepository;
import com.JobSwipe.webApp.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentAggregatorService {

    private final SeedListRepository seedListRepository;
    private final JobDepartmentMappingRepository jobDepartmentMappingRepository;
    private final JsonUtils jsonUtils;

    private static final int BATCH_SIZE = 100;

    public void fetchJobDepartmentMapping() {
        List<CompanySeedIdDTO> companySlugs = seedListRepository.findDistinctCompany();

        for (CompanySeedIdDTO companySeedIdDTO : companySlugs) {
            String company = companySeedIdDTO.getCompany();
            String departmentApiUrl = String.format("https://boards-api.greenhouse.io/v1/boards/%s/departments", company);

            try {
                JSONObject result = jsonUtils.fetchJson(departmentApiUrl);
                JSONArray departmentsArray = result.optJSONArray("departments");
                if (departmentsArray == null || departmentsArray.isEmpty()) {
                    log.info("No departments found for company: {}", company);
                    continue;
                }

                List<JobDepartmentMapping> toSave = new ArrayList<>(BATCH_SIZE);
                for (int i = 0; i < departmentsArray.length(); i++) {
                    JSONObject dept = departmentsArray.getJSONObject(i);
                    String departmentId = jsonUtils.getFieldValue(dept, "id");
                    String departmentName = jsonUtils.getFieldValue(dept,"name");

                    JSONArray deptJobs = dept.optJSONArray("jobs");
                    if (deptJobs == null || deptJobs.isEmpty()) continue;

                    for (int j = 0; j < deptJobs.length(); j++) {
                        JSONObject jobObj = deptJobs.getJSONObject(j);
                        String jobId = asString(jobObj.opt("id"));
                        if (jobId == null || jobId.isBlank()) continue;

                        // if job-department mapping already exists in the DB, skip to next job
                        if (jobDepartmentMappingRepository.existsByJobId(jobId)) {
                            log.info("Job with jobId={} already exists.", jobId);
                            continue;
                        }

                        JobDepartmentMapping mapping = JobDepartmentMapping.builder()
                                .id(UUID.randomUUID())
                                .jobId(jobId)
                                .departmentId(departmentId)
                                .department(departmentName)
                                .build();

                        toSave.add(mapping);

                        if (toSave.size() >= BATCH_SIZE) {
                            jobDepartmentMappingRepository.saveAll(toSave);
                            toSave.clear();
                        }
                    }
                }

                if (!toSave.isEmpty()) {
                    jobDepartmentMappingRepository.saveAll(toSave);
                }

                log.info("Saved job-department mappings for company: {}", company);

            } catch (Exception e) {
                log.error("Failed to fetch or save department mappings for {}: {}", company, e.getMessage(), e);
            }
        }
    }

    private static String asString(Object val) {
        return val == null ? null : String.valueOf(val);
    }
}