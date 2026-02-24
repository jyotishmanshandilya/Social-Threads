package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.service.DepartmentAggregatorService;
import com.JobSwipe.webApp.service.JobAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentAggregatorController {

    private final DepartmentAggregatorService departmentAggregatorService;

    @PostMapping("/aggregate")
    public ResponseEntity<String> aggregateJobs() {
        departmentAggregatorService.fetchJobDepartmentMapping();
        return ResponseEntity.ok("Department import started/completed (check logs for status).");
    }
}
