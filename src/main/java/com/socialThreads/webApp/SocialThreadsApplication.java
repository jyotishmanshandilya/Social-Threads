package com.socialThreads.webApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories(basePackages = "com.socialThreads.webApp.repository")
@EntityScan(basePackages = "com.socialThreads.webApp.model")
public class SocialThreadsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialThreadsApplication.class, args);
	}

}
