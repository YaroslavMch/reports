package com.easypark.reports.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "time.reports.dev")
public class TimeReportProperties {
    private String[] server;
    private String[] app;
    private String[] web;
}
