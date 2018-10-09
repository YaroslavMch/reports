package com.easypark.reports.entity.jira.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraResponse {
    private int startAt;
    private int total;
    private List<Issues> issues;
}
