package com.JobSwipe.webApp.service;

import com.JobSwipe.webApp.configuration.QueryConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryGeneratorService {

    private final QueryConfigProperties config;

    public List<String> buildQueries() {
        // LinkedHashSet preserves order and de-duplicates
        Set<String> queries = new LinkedHashSet<>();

        List<String> sites = Optional.ofNullable(config.getSites()).orElseGet(List::of);
        Map<String, List<String>> roles = Optional.ofNullable(config.getRoles()).orElseGet(Map::of);
        Map<String, List<String>> locations = Optional.ofNullable(config.getLocations()).orElseGet(Map::of);
        List<String> catchalls = Optional.ofNullable(config.getCatchalls()).orElseGet(List::of);

        for (String site : sites) {
            // Role × Location
            for (List<String> roleSyns : roles.values()) {
                for (String role : roleSyns) {
                    for (List<String> locSyns : locations.values()) {
                        for (String loc : locSyns) {
                            queries.add(String.format("site:%s %s %s", site, role, loc));
                        }
                    }
                }
            }
            // Catch-alls
            for (String c : catchalls) {
                queries.add(String.format("site:%s %s", site, c));
            }
        }
        return new ArrayList<>(queries);
    }

    public List<String> indiaKeywords() {
        Map<String, List<String>> locations = Optional.ofNullable(config.getLocations()).orElseGet(Map::of);

        return locations.values().stream()
                .flatMap(List::stream)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .distinct()
                .collect(Collectors.toList());
    }
}