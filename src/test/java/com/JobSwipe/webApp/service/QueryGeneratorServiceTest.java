package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.configuration.QueryConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryGeneratorServiceTest {

    @Mock
    private QueryConfigProperties config;

    @InjectMocks
    private QueryGeneratorService queryGeneratorService;

    @Test
    void buildQueries_generatesRoleLocationCrossProduct() {
        when(config.getSites()).thenReturn(List.of("boards.greenhouse.io"));
        when(config.getRoles()).thenReturn(Map.of(
                "backend", List.of("backend engineer", "backend developer"),
                "frontend", List.of("frontend engineer")
        ));
        when(config.getLocations()).thenReturn(Map.of(
                "india", List.of("India"),
                "bengaluru", List.of("Bengaluru", "Bangalore")
        ));
        when(config.getCatchalls()).thenReturn(List.of("engineer India"));

        List<String> queries = queryGeneratorService.buildQueries();

        // 1 site × 3 role synonyms × 3 location synonyms = 9 role×location queries
        // + 1 catchall = 10 total
        assertThat(queries).hasSize(10);
    }

    @Test
    void buildQueries_containsSitePrefix() {
        when(config.getSites()).thenReturn(List.of("boards.greenhouse.io"));
        when(config.getRoles()).thenReturn(Map.of(
                "backend", List.of("backend engineer", "backend developer"),
                "frontend", List.of("frontend engineer")
        ));
        when(config.getLocations()).thenReturn(Map.of(
                "india", List.of("India"),
                "bengaluru", List.of("Bengaluru", "Bangalore")
        ));
        when(config.getCatchalls()).thenReturn(List.of("engineer India"));

        List<String> queries = queryGeneratorService.buildQueries();
        assertThat(queries).allMatch(q -> q.startsWith("site:boards.greenhouse.io"));
    }

    @Test
    void buildQueries_containsCatchalls() {
        when(config.getSites()).thenReturn(List.of("boards.greenhouse.io"));
        when(config.getRoles()).thenReturn(Map.of(
                "backend", List.of("backend engineer")
        ));
        when(config.getLocations()).thenReturn(Map.of(
                "india", List.of("India")
        ));
        when(config.getCatchalls()).thenReturn(List.of("engineer India"));

        List<String> queries = queryGeneratorService.buildQueries();
        assertThat(queries).contains("site:boards.greenhouse.io engineer India");
    }

    @Test
    void buildQueries_deduplicatesQueries() {
        when(config.getSites()).thenReturn(List.of("boards.greenhouse.io"));
        when(config.getRoles()).thenReturn(Map.of(
                "group1", List.of("software engineer"),
                "group2", List.of("software engineer") // same synonym in two groups
        ));
        when(config.getLocations()).thenReturn(Map.of(
                "india", List.of("India")
        ));
        when(config.getCatchalls()).thenReturn(List.of());

        List<String> queries = queryGeneratorService.buildQueries();
        long uniqueCount = queries.stream().distinct().count();
        assertThat(uniqueCount).isEqualTo(queries.size());
    }

    @Test
    void buildQueries_handlesNullConfig() {
        when(config.getSites()).thenReturn(null);
        when(config.getRoles()).thenReturn(null);
        when(config.getLocations()).thenReturn(null);
        when(config.getCatchalls()).thenReturn(null);

        List<String> queries = queryGeneratorService.buildQueries();
        assertThat(queries).isEmpty();
    }

    @Test
    void buildQueries_handlesMultipleSites() {
        when(config.getSites()).thenReturn(List.of("boards.greenhouse.io", "job-boards.greenhouse.io"));
        when(config.getRoles()).thenReturn(Map.of(
                "backend", List.of("backend engineer")
        ));
        when(config.getLocations()).thenReturn(Map.of(
                "india", List.of("India")
        ));
        when(config.getCatchalls()).thenReturn(List.of());

        List<String> queries = queryGeneratorService.buildQueries();
        assertThat(queries).anyMatch(q -> q.startsWith("site:boards.greenhouse.io"));
        assertThat(queries).anyMatch(q -> q.startsWith("site:job-boards.greenhouse.io"));
    }

    @Test
    void indiaKeywords_returnsAllLocationSynonymsLowercased() {
        when(config.getLocations()).thenReturn(Map.of(
                "india", List.of("India"),
                "bengaluru", List.of("Bengaluru", "Bangalore")
        ));

        List<String> keywords = queryGeneratorService.indiaKeywords();
        assertThat(keywords).containsExactlyInAnyOrder("india", "bengaluru", "bangalore");
    }

    @Test
    void indiaKeywords_deduplicates() {
        when(config.getLocations()).thenReturn(Map.of(
                "loc1", List.of("India", "india") // duplicate after lowercase
        ));

        List<String> keywords = queryGeneratorService.indiaKeywords();
        assertThat(keywords).containsOnlyOnce("india");
    }
}
