package com.easypark.reports.service.impl;

import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.service.FileService;
import com.easypark.reports.service.MonthReportService;
import com.easypark.reports.service.UserService;
import com.easypark.reports.util.MonthParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private final MonthReportService monthReportService;
    private final UserService userService;
    private final WorkbookServiceImpl userReportServiceImpl;

    @Override
    public List<GroupWorkBook> getAllWorkBooks(String monthName, Integer year) {
        CustomMonth monthRange = MonthParser.getMonthRange(monthName, year);
        MonthReport monthReport = monthReportService.getUsersMonthReport(userService.getAllGroups(), monthRange);
        return userReportServiceImpl.createUsersWorkbooks(monthReport);
    }
}