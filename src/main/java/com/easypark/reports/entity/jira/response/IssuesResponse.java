package com.easypark.reports.entity.jira.response;

import com.easypark.reports.entity.Issue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class IssuesResponse {
    int startAt;
    int total;
    List<Issue> issues;

    public IssuesResponse(
            @JsonProperty("startAt") int startAt,
            @JsonProperty("total") int total,
            @JsonProperty("issues") List<Issue> issues
    ) {
        this.startAt = startAt;
        this.total = total;
        this.issues = issues;
    }
}
