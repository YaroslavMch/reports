package com.easypark.reports.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@Getter
@EqualsAndHashCode
public class CustomMonth {
    private final LocalDate fromDate;
    private final LocalDate toDate;

    public CustomMonth(Month month, int year) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("uuuu-M-d");
        this.fromDate = LocalDate.parse(year + "-" + month.getValue() + "-" + 1, pattern);
        this.toDate = LocalDate.parse(year + "-" + month.getValue() + "-" + month.maxLength(), pattern);
    }
}
