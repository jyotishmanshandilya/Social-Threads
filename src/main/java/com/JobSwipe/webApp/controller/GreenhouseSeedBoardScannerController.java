package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.service.SeedAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/greenhouse")
@RequiredArgsConstructor
public class GreenhouseSeedBoardScannerController {

    private final SeedAggregatorService seedAggregatorService;

    @PostMapping("/scan")
    public ResponseEntity<String> discoverBoards() {
        seedAggregatorService.discoverGreenhouseBoards();
        return ResponseEntity.ok("Greenhouse board discovery started/completed (check logs for details).");
    }
}