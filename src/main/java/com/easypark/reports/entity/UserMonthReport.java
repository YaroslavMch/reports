package com.easypark.reports.entity;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class UserMonthReport {
    User user;
    double illnessDays;
    double vacationDays;
    Map<Integer, Double> weeksHours;
    List<Report> reports;
}
