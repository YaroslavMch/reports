package com.easypark.reports.service;

import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.entity.UserMonthReport;

import java.util.List;

public interface MonthReportService {

    MonthReport getUsersMonthReport(List<String> users, CustomMonth month);

    UserMonthReport validateUserMonthReport(UserMonthReport userMonthReport, String regex);
}


