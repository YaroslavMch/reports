package com.easypark.reports.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "groups")
public class UserGroupProperties {
    private Map<String, String> general;
    private Map<String, String> easyParkersExperience;
    private Map<String, String> driversExperience;
    private Map<String, String> operatorsExperience;

}
