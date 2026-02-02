package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.service.SearchEngineService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google")
public class GoogleSearchController {

    @Autowired
    private SearchEngineService searchEngineService;

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam("query") String query,
            @RequestParam(value = "start", defaultValue = "1") int start
    ) {
        try {
            JSONObject result = searchEngineService.googleSearch(query, start);
            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("Error: " + e.getMessage());
        }
    }
}
