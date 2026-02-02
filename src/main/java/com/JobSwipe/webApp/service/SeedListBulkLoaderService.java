package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.exception.InvalidDataException;
import com.JobSwipe.webApp.model.enums.CsvContentType;
import com.JobSwipe.webApp.repository.SeedListRepository;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedListBulkLoaderService {

    private final CsvService csvService;

    /**
     * Loads and parses all CSV files in the provided directory using CsvService.
     *
     * @param directoryPath path to the directory containing the CSV files
     * @return all SeedList entries from all valid CSV files
     */
    public Integer loadSeedListsFromDirectory(String directoryPath, CsvContentType contentType) throws IOException {
        int seedListCount = 0;

        // List all *.csv files in the directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.csv")) {
            for (Path csvPath : stream) {
                if (Files.isRegularFile(csvPath) && Files.isReadable(csvPath)) {
                    String filename = csvPath.getFileName().toString().toLowerCase();
                    log.info("Parsing CSV file: {}", filename);
                    try (InputStream in = Files.newInputStream(csvPath, StandardOpenOption.READ)) {
                        seedListCount = csvService.parseSpreadsheet(in, contentType, contentType.equals(CsvContentType.JOB_DATA) ? filename : null);
                    } catch (CsvException | InvalidDataException e) {
                        log.error("Failed to parse '{}': {}", csvPath.getFileName(), e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to scan directory: {}", directoryPath, e);
            throw e;
        }

        return seedListCount;
    }
}