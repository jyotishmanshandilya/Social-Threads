package com.JobSwipe.webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories(basePackages = "com.JobSwipe.webApp.repository")
@EntityScan(basePackages = {"com.JobSwipe.webApp.entities", "com.JobSwipe.webApp.model"})
public class JobSwipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobSwipeApplication.class, args);
	}

}
