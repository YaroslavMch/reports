package com.easypark.reports.entity;

import lombok.Value;

import java.time.LocalDate;

@Value
public class Report {
    String issueKey;
    LocalDate started;
    double hoursSpent;
    String comment;
    String summary;

    public double getHoursSpent() {
        return Math.round(hoursSpent * 100) / (double) 100;
    }

    public String getComment() {
        return comment.replaceAll("\n", "");
    }
}
