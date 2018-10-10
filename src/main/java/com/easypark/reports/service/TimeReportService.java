package com.easypark.reports.service;

import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.TimeReport;

import java.util.List;
import java.util.Map;

public interface TimeReportService {
    Map<String, List<TimeReport>> getGroupedTimeReports(CustomMonth month);
}


