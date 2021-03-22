package com.easypark.reports.entity;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class UserMonthReport {
    String developerName;
    double workHours;
    double illnessDays;
    double vacationDays;
    Map<Integer, Double> weeksHours;
    List<TimeReport> reports;
}
