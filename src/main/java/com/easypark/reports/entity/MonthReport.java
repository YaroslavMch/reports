package com.easypark.reports.entity;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class MonthReport {
    int numberOfWeeks;
    List<UserMonthReport> usersReports;
    Map<String, Double> usersIllnessDays;
    Map<String, Double> usersVacationDays;
}
