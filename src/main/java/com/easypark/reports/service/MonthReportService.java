package com.easypark.reports.service;

import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.entity.User;
import com.easypark.reports.entity.UserMonthReport;
import org.apache.commons.lang3.Range;
import org.springframework.core.io.Resource;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

public interface MonthReportService {

    Resource getReportsResource(String monthName, Integer year);

    MonthReport getUsersMonthReport(List<User> users, Range<ChronoLocalDate> monthRange);
}


