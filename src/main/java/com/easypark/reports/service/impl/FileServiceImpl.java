package com.easypark.reports.service.impl;

import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.service.FileService;
import com.easypark.reports.service.TimeReportService;
import com.easypark.reports.service.TotalService;
import com.easypark.reports.util.GroupHelper;
import com.easypark.reports.util.MonthParser;
import com.easypark.reports.workbook.DevReports;
import com.easypark.reports.workbook.DevTotal;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private final TimeReportService timeReportService;
    private final TimeReportProperties timeReportProperties;
    private final TotalService totalService;

    @Override
    @SneakyThrows
    public List<GroupWorkBook> getAllWorkBooks(String monthName, Integer year, HttpServletResponse response) {
        if (Objects.isNull(year)) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        CustomMonth month = MonthParser.getMonthRange(monthName, year, response);
        Map<String, List<TimeReport>> timeReports = timeReportService.getGroupedTimeReports(month);
        if (timeReports.isEmpty()) {
            log.error("Not found reports!");
            response.setStatus(500);
            throw new RuntimeException("Not found reports!");
        }
        List<String> sortedUsers = Arrays.stream(GroupHelper.getAllGroups(timeReportProperties))
                .sorted()
                .collect(Collectors.toList());
        DevTotal devTotal = new DevTotal(timeReports, sortedUsers, totalService);
        DevReports devReports = new DevReports(timeReports, timeReportProperties, totalService);
        Thread devTotalThread = new Thread(devTotal);
        Thread devReportsThread = new Thread(devReports);
        devTotalThread.start();
        devReportsThread.start();
        devTotalThread.join();
        devReportsThread.join();
        List<GroupWorkBook> workBooks = devReports.getWorkBooks();
        workBooks.add(devTotal.getWorkBook());
        return workBooks;
    }
}