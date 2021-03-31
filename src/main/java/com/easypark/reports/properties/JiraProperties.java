package com.easypark.reports.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jira")
public class JiraProperties {
    private String domain;
    private String illnessKey;
    private String vacationKey;
    private String password;
}
