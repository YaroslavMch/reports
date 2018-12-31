package com.easypark.reports.service.impl;

import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.service.FileService;
import com.easypark.reports.service.TimeReportService;
import com.easypark.reports.util.GroupHelper;
import com.easypark.reports.util.MonthParser;
import com.easypark.reports.workbook.DevReports;
import com.easypark.reports.workbook.DevTotal;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private final TimeReportService timeReportService;
    private final TimeReportProperties timeReportProperties;

    @Override
    public List<GroupWorkBook> getAllWorkBooks(String monthName, Integer year, HttpServletResponse response) {
        if (Objects.isNull(monthName)) {
            monthName = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.US).toLowerCase();
        }
        if (Objects.isNull(year)) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        Map<String, List<TimeReport>> reports = getReports(MonthParser.getMonthRange(monthName, year, response), response);
        return getGroupWorkBooks(reports);
    }

    @SneakyThrows
    private List<GroupWorkBook> getGroupWorkBooks(Map<String, List<TimeReport>> timeReports) {
        DevTotal devTotal = new DevTotal(timeReports, getSortedUsers());
        DevReports devReports = new DevReports(timeReports, timeReportProperties);
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

    private Map<String, List<TimeReport>> getReports(CustomMonth month, HttpServletResponse response) {
        Map<String, List<TimeReport>> timeReports = timeReportService.getGroupedTimeReports(month);
        if (timeReports.isEmpty()) {
            log.error("Not found reports!");
            response.setStatus(500);
            throw new RuntimeException("Not found reports!");
        }
        return timeReports;
    }

    private List<String> getSortedUsers() {
        return Arrays.stream(GroupHelper.getAllGroups(timeReportProperties))
                .sorted()
                .collect(Collectors.toList());
    }
}