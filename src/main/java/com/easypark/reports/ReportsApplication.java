package com.easypark.reports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import com.easypark.reports.properties.JiraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class ReportsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportsApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder, JiraProperties jiraProperties){
		return builder
				.basicAuthentication(jiraProperties.getEmail(), jiraProperties.getApiToken())
				.setConnectTimeout(Duration.ofMinutes(3))
				.setReadTimeout(Duration.ofMinutes(3))
				.build();
	}
}
