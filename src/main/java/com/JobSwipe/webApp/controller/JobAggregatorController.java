package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.service.JobAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobAggregatorController {

    private final JobAggregatorService jobAggregatorService;

    @PostMapping("/aggregate")
    public ResponseEntity<String> aggregateJobs() {
        jobAggregatorService.importJobs();
        return ResponseEntity.ok("Job import started/completed (check logs for status).");
    }
}