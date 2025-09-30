package com.easypark.reports.entity.jira.worklog;

import com.easypark.reports.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDate;

@Value
public class WorkLog {
    User author;
    AdfDocument comment;
    LocalDate started;
    double timeSpentSeconds;

    public WorkLog(
            @JsonProperty("author") User author,
            @JsonProperty("comment") AdfDocument comment,
            @JsonProperty("started") String started,
            @JsonProperty("timeSpentSeconds") double timeSpentSeconds
    ) {
        this.author = author;
        this.comment = comment;
        this.started = LocalDate.parse(started.split("T")[0]);
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public String getCommentText() {
        return comment != null ? comment.extractText() : "";
    }
}
