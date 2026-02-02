package com.JobSwipe.webApp.controller;

import com.JobSwipe.webApp.model.DirectoryPath;
import com.JobSwipe.webApp.service.SeedListBulkLoaderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/seedlist")
@AllArgsConstructor
public class SeedListController {

    public SeedListBulkLoaderService seedListBulkLoaderService;

    @PostMapping(value = "/loadBulkSeedLists", consumes = "application/json")
    public void loadBulkSeedLists(@RequestBody DirectoryPath directoryPath) throws IOException {
        log.info("Received request to load SeedLists from directory: {} : {}", directoryPath.getPath(), directoryPath.getContentType());
        Integer seedListCount = seedListBulkLoaderService.loadSeedListsFromDirectory(directoryPath.getPath(), directoryPath.getContentType());
        log.info("Total SeedList entries loaded: {}", seedListCount);
    }
}
