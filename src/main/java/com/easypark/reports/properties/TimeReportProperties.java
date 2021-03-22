package com.easypark.reports.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "time.reports.dev")
public class TimeReportProperties {
    private List<String> server;
    private List<String> app;
    private List<String> web;
    private List<String> general;
    private List<String> innovation;
}
