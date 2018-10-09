package com.easypark.reports.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class Month {
    private final LocalDate fromDate;
    private final LocalDate toDate;
}
