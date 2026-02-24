package com.JobSwipe.webApp.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "search")
public class QueryConfigProperties {
    private List<String> sites;
    private Map<String, List<String>> roles;
    private Map<String, List<String>> locations;
    private List<String> catchalls;

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    public void setRoles(Map<String, List<String>> roles) {
        this.roles = roles;
    }

    public void setLocations(Map<String, List<String>> locations) {
        this.locations = locations;
    }

    public void setCatchalls(List<String> catchalls) {
        this.catchalls = catchalls;
    }
}
