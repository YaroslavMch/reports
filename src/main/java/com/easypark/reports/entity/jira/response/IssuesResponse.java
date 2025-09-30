package com.easypark.reports.entity.jira.response;

import com.easypark.reports.entity.Issue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class IssuesResponse {
    boolean isLast;
    List<Issue> issues;

    public IssuesResponse(
            @JsonProperty("isLast") boolean isLast,
            @JsonProperty("issues") List<Issue> issues
    ) {
        this.isLast = isLast;
        this.issues = issues;
    }
}
