package com.easypark.reports.entity.jira.response;

import com.easypark.reports.entity.Issue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class IssuesResponse {
    int total;
    List<Issue> issues;

    public IssuesResponse(
            @JsonProperty("total") int total,
            @JsonProperty("issues") List<Issue> issues
    ) {
        this.total = total;
        this.issues = issues;
    }
}
