package com.easypark.reports.service.impl;

import com.easypark.reports.entity.GroupWorkbook;
import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.entity.UserGroup;
import com.easypark.reports.service.*;
import com.easypark.reports.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final MonthReportService monthReportService;
    private final UserService userService;
    private final WorkbookService userReportService;
    private final ZipService zipService;

    @Override
    public Resource getReportsResource(String monthName, Integer year) {
        Range<ChronoLocalDate> monthRange = DateUtils.createMonthRange(monthName, year);
        MonthReport monthReport = monthReportService.getUsersMonthReport(userService.getGroup(UserGroup.GENERAL), monthRange);
        List<GroupWorkbook> usersWorkbooks = userReportService.createUsersWorkbooks(monthReport);
        return zipService.writeToZip(usersWorkbooks);
    }
}