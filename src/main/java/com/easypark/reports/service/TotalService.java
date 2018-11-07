package com.easypark.reports.service;

import com.easypark.reports.entity.DevTimeTotal;
import com.easypark.reports.entity.TimeReport;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface TotalService {
    DevTimeTotal getDevTimes(List<TimeReport> timeReports, String user);

    String countTotal(Sheet sheet, int cellNum);
}
