package com.easypark.reports.entity;

import lombok.Value;
import org.apache.commons.lang3.Range;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Value
public class MonthReport {
    Range<ChronoLocalDate> monthRange;
    List<UserMonthReport> usersReports;
}
