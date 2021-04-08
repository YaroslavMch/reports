package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkbook;
import com.easypark.reports.entity.MonthReport;

import java.util.List;

public interface GroupWorkbookService {

    List<GroupWorkbook> createUsersWorkbooks(MonthReport monthReport);
}
