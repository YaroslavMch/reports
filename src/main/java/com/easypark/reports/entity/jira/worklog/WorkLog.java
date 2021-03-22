package com.easypark.reports.entity.jira.worklog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDate;
import java.util.Map;

@Value
public class WorkLog {
    String authorName;
    String comment;
    LocalDate started;
    double timeSpentSeconds;

    public WorkLog(
            @JsonProperty("author") Map<String, Object> author,
            @JsonProperty("comment") String comment,
            @JsonProperty("started") String started,
            @JsonProperty("timeSpentSeconds") int timeSpentSeconds
    ) {
        this.authorName = (String) author.get("displayName");
        this.comment = comment;
        this.started = LocalDate.parse(started.split("T")[0]);
        this.timeSpentSeconds = timeSpentSeconds;
    }
}
