package com.socialThreads.webApp.flyway;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("flyway")
public class FlywayApplication {
    public static void main(String[] args){
        new SpringApplicationBuilder(FlywayApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
