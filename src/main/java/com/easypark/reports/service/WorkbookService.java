package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.entity.MonthReport;

import java.util.List;

public interface WorkbookService {

    List<GroupWorkBook> createUsersWorkbooks(MonthReport monthReport);
}
