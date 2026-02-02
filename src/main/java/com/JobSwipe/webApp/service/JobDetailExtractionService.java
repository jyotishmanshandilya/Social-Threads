package com.JobSwipe.webApp.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class JobDetailExtractionService {

    // List of robust regex patterns for years-of-experience matching
    private static final List<Pattern> patterns = Arrays.asList(
            // Year range: "0-3 years", "0 to 3 years"
            Pattern.compile("\\b(\\d+)\\s*(-|–|to)\\s*(\\d+)\\s*years?(?:\\s+of)?\\s*experience\\b", Pattern.CASE_INSENSITIVE),
            // Month range: "0-6 months", "0 to 6 months"
            Pattern.compile("\\b(\\d+)\\s*(-|–|to)\\s*(\\d+)\\s*months?(?:\\s+of)?\\s*experience\\b", Pattern.CASE_INSENSITIVE),

            // Single year: "0 years...", "1+ years..."
            Pattern.compile("\\b(\\d+)\\s*\\+?\\s*years?(?:\\s+of)?\\s*experience\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bminimum\\s*(\\d+)\\s*years?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(\\d+)\\s*years?(?:\\s+of)?(?:related\\s*)?work\\s*experience\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bat\\s*least\\s*(\\d+)\\s*years?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\brequires?\\s*(\\d+)\\s*years?", Pattern.CASE_INSENSITIVE),

            // Single month: "0 months...", "6 months...", etc.
            Pattern.compile("\\b(\\d+)\\s*\\+?\\s*months?(?:\\s+of)?\\s*experience\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bminimum\\s*(\\d+)\\s*months?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bat\\s*least\\s*(\\d+)\\s*months?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\brequires?\\s*(\\d+)\\s*months?", Pattern.CASE_INSENSITIVE),

            Pattern.compile("\\b(\\d+)\\s*\\+?\\s*years?(?:\\s+in)?\\s+[\\w\\s-]+roles?\\b", Pattern.CASE_INSENSITIVE), // '5+ years in related finance roles'
            Pattern.compile("\\b(\\d+)\\s*\\+?\\s*years?(?:\\s+in)?\\s+[\\w\\s-]+\\b", Pattern.CASE_INSENSITIVE),        // '3+ years in accounting'
            Pattern.compile("\\b(\\d+)\\s*\\+?\\s*years?\\b", Pattern.CASE_INSENSITIVE)                                 // catch-all: '2+ years'
    );

    private static final List<String> requirementsHeadings = Arrays.asList(
            "requirements", "qualifications", "what you’ll need", "what you bring", "who you are",
            "you'll thrive", "skills required", "skills & experience", "essential skills", "you'll need"
    );

    public Integer extractYearsOfExperience(String htmlContent) {
        String text = extractRelevantText(htmlContent);

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                if (matcher.groupCount() > 1 && matcher.group(2) != null) {
                    return Integer.parseInt(matcher.group(1));
                } else {
                    return Integer.parseInt(matcher.group(1));
                }
            }
        }
        return null;
    }

    public String extractRelevantText(String htmlContent) {
        String unescaped = org.jsoup.parser.Parser.unescapeEntities(htmlContent, false);
        Document doc = Jsoup.parse(unescaped);

        // Only keep sections after a "requirements" or similar heading
        Elements elements = doc.body().select("*");
        boolean inRelevantSection = false;
        StringBuilder relevantText = new StringBuilder();
        for (Element el : elements) {
            // Detect headings that signal requirements
            if (el.tagName().matches("h2|h3|strong|b")) {
                String heading = el.text().toLowerCase().trim();
                if (requirementsHeadings.stream().anyMatch(heading::contains)) {
                    inRelevantSection = true;
                    continue;
                }
            }
            // Start collecting li/p under the relevant heading
            if (inRelevantSection && (el.tagName().equals("li") || el.tagName().equals("p"))) {
                relevantText.append(el.text()).append("\n");
            }
        }
        return relevantText.toString();
    }
}
