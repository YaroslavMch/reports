package com.easypark.reports.service;

import com.easypark.reports.entity.Month;
import com.easypark.reports.entity.TimeReport;

import java.util.List;
import java.util.Map;

public interface TimeReportService {
   Map<String, List<TimeReport>> getGroupedTimeReports(Month monthRange);

   List<String> getAllTimeReportKeys(Map<String, List<TimeReport>> groupedTimeReports);
}


