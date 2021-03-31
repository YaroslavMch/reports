package com.easypark.reports.service;

import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.entity.User;
import com.easypark.reports.entity.UserGroup;
import com.easypark.reports.entity.UserMonthReport;
import org.apache.commons.lang3.Range;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

public interface MonthReportService {

    MonthReport getUsersMonthReport(List<User> users, Range<ChronoLocalDate> monthRange);

    UserMonthReport filterUserMonthReport(UserMonthReport userMonthReport, UserGroup group);
}


