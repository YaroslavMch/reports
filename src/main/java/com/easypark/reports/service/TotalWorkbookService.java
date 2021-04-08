package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkbook;
import com.easypark.reports.entity.MonthReport;

public interface TotalWorkbookService {

    GroupWorkbook createTotalWorkbook(MonthReport monthReport);
}
