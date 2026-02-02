package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.CompanyJobs;
import com.JobSwipe.webApp.entities.SeedList;
import com.JobSwipe.webApp.exception.InvalidDataException;
import com.JobSwipe.webApp.model.enums.CsvContentType;
import com.JobSwipe.webApp.repository.CompanyJobsRepository;
import com.JobSwipe.webApp.repository.SeedListRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class CsvService {

    private SeedListRepository seedListRepository;
    private CompanyJobsRepository companyJobsRepository;

    public Integer parseSpreadsheet(InputStream inputStream, CsvContentType contentType, String filename) throws IOException, CsvException, InvalidDataException {
        log.info("Parsing contents of temp file...");
        Integer recordCount;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            recordCount = parseSpreadsheet(reader, contentType, filename);
        }
        return recordCount;
    }

    private Integer parseSpreadsheet(BufferedReader fileReader, CsvContentType contentType, String filename) throws CsvException, IOException {
        CSVParser parser = new CSVParserBuilder()
                .withEscapeChar('\0')
                .build();

        List<String[]> allData;
        try (CSVReader csvReader = new CSVReaderBuilder(fileReader)
                .withCSVParser(parser)
                .build()){
            allData = csvReader.readAll();
        }

        if (allData.isEmpty() || allData.size() == 1) {
            throw new IOException("CSV file is empty");
        }

        // Read header and create a column index map
        String[] headers = allData.getFirst();
        Map<String, Integer> headerIndexMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerIndexMap.put(headers[i].trim().toLowerCase(Locale.ROOT), i);
        }

        for (int i = 1; i < allData.size(); i++) { // Skip header row
            String[] row = allData.get(i);
            parseContent(row, headerIndexMap, contentType, filename);
        }

        return allData.size() - 1; // Exclude header row
    }

    private void parseContent(String[] row, Map<String, Integer> headerIndexMap, CsvContentType contentType, String filename) throws InvalidDataException {
        try {
            if (contentType == CsvContentType.SEED_LIST) {
                SeedList seed = SeedList.builder()
                        .id(UUID.randomUUID())
                        .company(parseStringValue(row, headerIndexMap, "greenhouse_board"))
                        .validationStatus(parseBooleanValue(row, headerIndexMap, "verified"))
                        .jobBoard("greenhouse_board")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                seedListRepository.save(seed);
            }
            else {
                UUID seedListId = extractSeedListIdFromFilename(filename);
                CompanyJobs companyJobs = CompanyJobs.builder()
                        .id(UUID.randomUUID())
                        .seedListId(seedListId)
                        .jobTitle(parseStringValue(row, headerIndexMap, "title"))
                        .location(parseStringValue(row, headerIndexMap, "location"))
                        .applicationUrl(parseStringValue(row, headerIndexMap, "url"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                companyJobsRepository.save(companyJobs);
            }

        } catch (InvalidDataException e) {
            log.error("Error parsing row", e);
            throw e;
        }
    }

    private String parseStringValue(String[] row, Map<String, Integer> headerIndexMap, String columnName) {
        Integer index = headerIndexMap.get(columnName.toLowerCase(Locale.ROOT));
        return (index != null && index < row.length) ? row[index].trim().toLowerCase() : "";
    }

    private boolean parseBooleanValue(String[] row, Map<String, Integer> headerIndexMap, String columnName) {
        Integer index = headerIndexMap.get(columnName.toLowerCase(Locale.ROOT));
        return index != null && index < row.length && row[index].trim().equalsIgnoreCase("true");
    }

    private UUID extractSeedListIdFromFilename(String fileName) throws InvalidDataException {
        try {
            String company = fileName.split("_")[0].trim();
            log.info("Company Name is {}", company);
            return seedListRepository.findByCompany(company)
                    .orElseThrow(() -> new InvalidDataException("Seed list not found for company: " + company))
                    .getId();
        } catch (Exception e) {
            throw new InvalidDataException("Invalid file name format: " + fileName);
        }
    }
}