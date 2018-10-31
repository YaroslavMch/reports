package com.easypark.reports.entity.jira.worklog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkReport {
    private Author author;
    private String comment;
    private String started;
    private int timeSpentSeconds;

    public LocalDate getStarted() {
        return LocalDate.parse(started.split("T")[0]);
    }
}
