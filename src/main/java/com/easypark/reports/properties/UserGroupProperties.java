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
    private Map<String, String> server;
    private Map<String, String> app;
    private Map<String, String> web;
    private Map<String, String> general;
    private Map<String, String> innovation;
}
