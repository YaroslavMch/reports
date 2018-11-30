package com.easypark.reports.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TimeReport {
    private final String keyAuthor;
    private final String taskKey;
    private final LocalDate date;
    private final double timeSpent;
    private final String comment;
    private final String summary;

    public double getTimeSpent() {
        return Math.round(timeSpent * 100) / (double) 100;
    }

    public String getComment() {
        return comment.replaceAll("\n", "");
    }
}
