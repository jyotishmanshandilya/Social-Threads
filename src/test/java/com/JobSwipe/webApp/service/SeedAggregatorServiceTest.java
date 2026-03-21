package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.entities.SearchProgress;
import com.JobSwipe.webApp.entities.SeedList;
import com.JobSwipe.webApp.model.enums.SearchQueryStatus;
import com.JobSwipe.webApp.repository.SearchProgressRepository;
import com.JobSwipe.webApp.repository.SeedListRepository;
import com.JobSwipe.webApp.util.BackoffUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeedAggregatorServiceTest {

    @Mock private SearchEngineService searchEngineService;
    @Mock private SeedListRepository seedListRepository;
    @Mock private QueryGeneratorService queryGeneratorService;
    @Mock private BackoffUtils backoffUtils;
    @Mock private SearchProgressRepository searchProgressRepository;

    @InjectMocks
    private SeedAggregatorService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "googleApiKey", "test-api-key");
        ReflectionTestUtils.setField(service, "googleCx", "test-cx");
    }

    // --- extractGreenhouseBoard ---

    @Test
    void extractGreenhouseBoard_extractsSlugFromBoardsUrl() {
        assertThat(service.extractGreenhouseBoard("https://boards.greenhouse.io/stripe"))
                .isEqualTo("stripe");
    }

    @Test
    void extractGreenhouseBoard_extractsSlugFromJobBoardsUrl() {
        assertThat(service.extractGreenhouseBoard("https://job-boards.greenhouse.io/airbnb"))
                .isEqualTo("airbnb");
    }

    @Test
    void extractGreenhouseBoard_extractsSlugFromJobDetailUrl() {
        assertThat(service.extractGreenhouseBoard("https://boards.greenhouse.io/stripe/jobs/12345"))
                .isEqualTo("stripe");
    }

    @Test
    void extractGreenhouseBoard_returnsNullForNonGreenhouseUrl() {
        assertThat(service.extractGreenhouseBoard("https://www.linkedin.com/jobs/view/123"))
                .isNull();
    }

    @Test
    void extractGreenhouseBoard_returnsNullForBlankUrl() {
        assertThat(service.extractGreenhouseBoard("")).isNull();
    }

    // --- hasIndiaJobs ---

    @Test
    void hasIndiaJobs_returnsTrueWhenJobLocationContainsIndiaKeyword() {
        JSONObject jobsData = new JSONObject("""
            {"jobs": [{"location": {"name": "Bengaluru, India"}}]}
        """);
        assertThat(service.hasIndiaJobs(jobsData, List.of("india", "bengaluru"))).isTrue();
    }

    @Test
    void hasIndiaJobs_returnsFalseWhenNoIndiaJobs() {
        JSONObject jobsData = new JSONObject("""
            {"jobs": [{"location": {"name": "San Francisco, CA"}}]}
        """);
        assertThat(service.hasIndiaJobs(jobsData, List.of("india", "bengaluru"))).isFalse();
    }

    @Test
    void hasIndiaJobs_returnsFalseForNullJobsData() {
        assertThat(service.hasIndiaJobs(null, List.of("india"))).isFalse();
    }

    @Test
    void hasIndiaJobs_returnsFalseForEmptyJobsArray() {
        JSONObject jobsData = new JSONObject("{\"jobs\": []}");
        assertThat(service.hasIndiaJobs(jobsData, List.of("india"))).isFalse();
    }

    @Test
    void hasIndiaJobs_returnsFalseWhenJobsKeyMissing() {
        JSONObject jobsData = new JSONObject("{}");
        assertThat(service.hasIndiaJobs(jobsData, List.of("india"))).isFalse();
    }

    @Test
    void hasIndiaJobs_isCaseInsensitive() {
        JSONObject jobsData = new JSONObject("""
            {"jobs": [{"location": {"name": "BANGALORE"}}]}
        """);
        assertThat(service.hasIndiaJobs(jobsData, List.of("bangalore"))).isTrue();
    }

    // --- validateBoard ---

    @Test
    void validateBoard_returnsJsonObjectOnSuccess() throws IOException {
        when(searchEngineService.httpGet(anyString()))
                .thenReturn("{\"jobs\": []}");

        JSONObject result = service.validateBoard("stripe");
        assertThat(result).isNotNull();
    }

    @Test
    void validateBoard_returnsNullOnIOException() throws IOException {
        when(searchEngineService.httpGet(anyString()))
                .thenThrow(new IOException("connection refused"));

        JSONObject result = service.validateBoard("nonexistent");
        assertThat(result).isNull();
    }

    // --- saveBoard ---

    @Test
    void saveBoard_savesNormalizedLowercaseCompanyName() {
        ArgumentCaptor<SeedList> captor = ArgumentCaptor.forClass(SeedList.class);
        service.saveBoard("Stripe");
        verify(seedListRepository).save(captor.capture());

        SeedList saved = captor.getValue();
        assertThat(saved.getCompany()).isEqualTo("stripe");
        assertThat(saved.getJobBoard()).isEqualTo("greenhouse_board");
        assertThat(saved.isValidationStatus()).isTrue();
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void saveBoard_trimsWhitespace() {
        ArgumentCaptor<SeedList> captor = ArgumentCaptor.forClass(SeedList.class);
        service.saveBoard("  airbnb  ");
        verify(seedListRepository).save(captor.capture());
        assertThat(captor.getValue().getCompany()).isEqualTo("airbnb");
    }

    // --- discoverGreenhouseBoards: guard clauses ---

    @Test
    void discoverGreenhouseBoards_abortsWhenApiKeyMissing() {
        ReflectionTestUtils.setField(service, "googleApiKey", "");
        service.discoverGreenhouseBoards();
        verifyNoInteractions(queryGeneratorService);
    }

    @Test
    void discoverGreenhouseBoards_abortsWhenCxMissing() {
        ReflectionTestUtils.setField(service, "googleCx", "");
        service.discoverGreenhouseBoards();
        verifyNoInteractions(queryGeneratorService);
    }

    @Test
    void discoverGreenhouseBoards_skipsAlreadyCompletedQuery() throws Exception {
        when(queryGeneratorService.buildQueries()).thenReturn(List.of("site:boards.greenhouse.io engineer India"));
        when(queryGeneratorService.indiaKeywords()).thenReturn(List.of("india"));
        when(searchProgressRepository.existsByQueryAndStatusOrderByLastRunDesc(anyString(), eq(SearchQueryStatus.COMPLETED)))
                .thenReturn(true);

        service.discoverGreenhouseBoards();

        verify(searchEngineService, never()).googleSearch(anyString(), anyInt());
    }

    @Test
    void discoverGreenhouseBoards_savesCheckpointAndAbortsOn429() throws Exception {
        when(queryGeneratorService.buildQueries()).thenReturn(List.of("site:boards.greenhouse.io engineer India"));
        when(queryGeneratorService.indiaKeywords()).thenReturn(List.of("india"));
        when(searchProgressRepository.existsByQueryAndStatusOrderByLastRunDesc(anyString(), eq(SearchQueryStatus.COMPLETED)))
                .thenReturn(false);
        when(searchProgressRepository.findTopByQueryAndStatusOrderByLastRunDesc(anyString(), eq(SearchQueryStatus.FAILED)))
                .thenReturn(Optional.empty());
        when(searchProgressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        when(searchEngineService.googleSearch(anyString(), anyInt()))
                .thenThrow(new RuntimeException("HTTP 429 Too Many Requests"));

        service.discoverGreenhouseBoards();

        ArgumentCaptor<SearchProgress> captor = ArgumentCaptor.forClass(SearchProgress.class);
        verify(searchProgressRepository, atLeastOnce()).save(captor.capture());

        // Last save should mark as FAILED
        List<SearchProgress> saved = captor.getAllValues();
        boolean hasFailed = saved.stream().anyMatch(p -> p.getStatus() == SearchQueryStatus.FAILED);
        assertThat(hasFailed).isTrue();
    }
}
