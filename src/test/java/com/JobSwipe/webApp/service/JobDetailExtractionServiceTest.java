package com.JobSwipe.webApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class JobDetailExtractionServiceTest {

    private JobDetailExtractionService service;

    @BeforeEach
    void setUp() {
        service = new JobDetailExtractionService();
    }

    // --- extractYearsOfExperience ---

    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource({
        "'<h2>Requirements</h2><ul><li>3-5 years of experience</li></ul>', 3",
        "'<h2>Requirements</h2><ul><li>2 to 4 years of experience</li></ul>', 2",
        "'<h2>Requirements</h2><ul><li>5+ years experience</li></ul>', 5",
        "'<h2>Requirements</h2><ul><li>minimum 3 years</li></ul>', 3",
        "'<h2>Requirements</h2><ul><li>at least 4 years</li></ul>', 4",
        "'<h2>Requirements</h2><ul><li>requires 2 years</li></ul>', 2",
        "'<h2>Requirements</h2><ul><li>1 year of experience</li></ul>', 1",
        "'<h2>Requirements</h2><ul><li>7 years work experience</li></ul>', 7"
    })
    void extractYearsOfExperience_recognizesCommonPatterns(String html, int expected) {
        assertThat(service.extractYearsOfExperience(html)).isEqualTo(expected);
    }

    @Test
    void extractYearsOfExperience_returnsNullWhenNoMatch() {
        String html = "<h2>Requirements</h2><ul><li>Good communication skills</li></ul>";
        assertThat(service.extractYearsOfExperience(html)).isNull();
    }

    @Test
    void extractYearsOfExperience_returnsNullForEmptyContent() {
        assertThat(service.extractYearsOfExperience("")).isNull();
    }

    @Test
    void extractYearsOfExperience_returnsNullWhenNoRequirementsHeading() {
        // Experience mentioned but not under a requirements heading — should not match
        String html = "<h2>About Us</h2><ul><li>5 years of experience preferred</li></ul>";
        // Service only searches under requirement-like headings
        assertThat(service.extractYearsOfExperience(html)).isNull();
    }

    @Test
    void extractYearsOfExperience_handlesRangeWithEnDash() {
        String html = "<h2>Qualifications</h2><ul><li>2–4 years of experience</li></ul>";
        assertThat(service.extractYearsOfExperience(html)).isEqualTo(2);
    }

    @Test
    void extractYearsOfExperience_monthPatternReturnsMonthCount() {
        String html = "<h2>Requirements</h2><ul><li>6 months of experience</li></ul>";
        assertThat(service.extractYearsOfExperience(html)).isEqualTo(6);
    }

    // --- extractRelevantText ---

    @Test
    void extractRelevantText_extractsTextUnderRequirementsHeading() {
        String html = "<h2>Requirements</h2><ul><li>5 years Java</li><li>Spring Boot</li></ul>";
        String text = service.extractRelevantText(html);
        assertThat(text).contains("5 years Java").contains("Spring Boot");
    }

    @Test
    void extractRelevantText_doesNotExtractTextBeforeRequirementsHeading() {
        String html = "<h2>About Role</h2><p>We build great products</p>"
                + "<h2>Requirements</h2><ul><li>3 years Python</li></ul>";
        String text = service.extractRelevantText(html);
        assertThat(text).doesNotContain("We build great products");
        assertThat(text).contains("3 years Python");
    }

    @Test
    void extractRelevantText_handlesQualificationsHeading() {
        String html = "<h3>Qualifications</h3><p>At least 4 years in cloud</p>";
        String text = service.extractRelevantText(html);
        assertThat(text).contains("At least 4 years in cloud");
    }

    @Test
    void extractRelevantText_returnsEmptyStringForHtmlWithNoRelevantSection() {
        String html = "<h2>Benefits</h2><p>Health insurance</p>";
        String text = service.extractRelevantText(html);
        assertThat(text).isEmpty();
    }

    @Test
    void extractRelevantText_handlesNullByThrowing() {
        // The current implementation calls Jsoup.parse(null) which throws
        assertThatThrownBy(() -> service.extractRelevantText(null))
                .isInstanceOf(Exception.class);
    }
}
