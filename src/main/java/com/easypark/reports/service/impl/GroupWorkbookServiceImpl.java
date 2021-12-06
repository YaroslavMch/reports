package com.easypark.reports.service.impl;

import com.easypark.reports.entity.*;
import com.easypark.reports.service.GroupWorkbookService;
import com.easypark.reports.service.UserService;
import com.easypark.reports.util.DateUtils;
import com.easypark.reports.util.GroupSheetUtils;
import com.easypark.reports.util.SummarySheetUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.easypark.reports.util.Constant.START_ROW_NUM;
import static com.easypark.reports.util.Constant.SUMMARY_SHEET_NAME;
import static com.easypark.reports.util.DateUtils.WEEK_OF_MONTH;
import static com.easypark.reports.util.SheetUtils.autoSizeColumns;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupWorkbookServiceImpl implements GroupWorkbookService {
    private final UserService userService;

    public List<GroupWorkbook> createUsersWorkbooks(MonthReport monthReport) {
        return Arrays.stream(UserGroup.values())
                .parallel()
                .map(group -> createGroupWorkbook(group, monthReport))
                .collect(Collectors.toList());
    }

    private GroupWorkbook createGroupWorkbook(UserGroup group, MonthReport monthReport) {
        Workbook workbook = new XSSFWorkbook();
        writeMonthSummary(group, monthReport, workbook);
        writeMonthReport(group, monthReport, workbook);
        autoSizeColumns(workbook);
        return new GroupWorkbook(group.getName(), workbook);
    }

    private void writeMonthSummary(UserGroup group, MonthReport monthReport, Workbook workbook) {
        int rowNum = START_ROW_NUM;
        Sheet summarySheet = workbook.createSheet(SUMMARY_SHEET_NAME);
        int numberOfWeeks = DateUtils.getNumberOfWeeks(monthReport.getMonthRange());
        rowNum = SummarySheetUtils.addHeaderRow(rowNum, summarySheet, numberOfWeeks) + 1;
        for (UserMonthReport userReport : findUsersReports(monthReport, group)) {
            UserMonthReport filteredUserReport = filterUserMonthReport(userReport, group);
            rowNum = SummarySheetUtils.addRow(rowNum, summarySheet, filteredUserReport, numberOfWeeks) + 1;
        }
        SummarySheetUtils.addTotalRow(rowNum, summarySheet, numberOfWeeks);
    }

    private void writeMonthReport(UserGroup group, MonthReport monthReport, Workbook workbook) {
        int numberOfWeeks = DateUtils.getNumberOfWeeks(monthReport.getMonthRange());
        for (UserMonthReport userMonthReport : findUsersReports(monthReport, group)) {
            UserMonthReport filteredUserReport = filterUserMonthReport(userMonthReport, group);
            if (!filteredUserReport.getReports().isEmpty()) {
                writeUserReports(workbook, filteredUserReport, numberOfWeeks);
            }
        }
    }

    private void writeUserReports(Workbook workbook, UserMonthReport filteredUserReport, int numberOfWeeks) {
        int rowNum = START_ROW_NUM;
        Sheet userSheet = workbook.createSheet(filteredUserReport.getUser().getDisplayName());
        rowNum = GroupSheetUtils.addHeaderRow(rowNum, userSheet) + 1;
        List<String> weekTotalsAddresses = new ArrayList<>();
        Map<Integer, List<Report>> weeksReports = getWeeksReports(filteredUserReport, numberOfWeeks);
        for (Integer week : weeksReports.keySet()) {
            int weekStartIndex = rowNum;
            for (Report report : weeksReports.get(week)) {
                rowNum = GroupSheetUtils.addRow(rowNum, report, userSheet) + 1;
            }
            if (!weeksReports.get(week).isEmpty()) {
                Row row = GroupSheetUtils.addWeekTotalRow(rowNum, weekStartIndex, userSheet, week);
                weekTotalsAddresses.add(row.getCell(4).getAddress().formatAsString());
                rowNum = row.getRowNum() + 1;
            }
        }
        GroupSheetUtils.addMonthTotalRow(rowNum, weekTotalsAddresses, userSheet);
    }

    private Map<Integer, List<Report>> getWeeksReports(UserMonthReport filteredUserReport, int numberOfWeeks) {
        Map<Integer, List<Report>> weeksReports = new LinkedHashMap<>();
        for (int i = 1; i <= numberOfWeeks; i++) {
            weeksReports.put(i, findUserWeekReports(i, filteredUserReport));
        }
        return weeksReports;
    }

    private List<Report> findUserWeekReports(int week, UserMonthReport userMonthReport) {
        return userMonthReport.getReports().parallelStream()
                .filter(report -> report.getStarted().get(WEEK_OF_MONTH) == week)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<UserMonthReport> findUsersReports(MonthReport monthReport, UserGroup group) {
        return monthReport.getUsersReports().parallelStream()
                .filter(report -> userService.getGroup(group).contains(report.getUser()))
                .collect(Collectors.toUnmodifiableList());
    }

    private UserMonthReport filterUserMonthReport(UserMonthReport userMonthReport, UserGroup group) {
        Map<Integer, Double> weeksWorkHours = new HashMap<>();
        List<Report> filteredReports = new ArrayList<>();
        for (Report report : userMonthReport.getReports()) {
            if (report.getIssueKey().matches(group.getRegex())) {
                filteredReports.add(report);
                int week = report.getStarted().get(WEEK_OF_MONTH);
                weeksWorkHours.merge(week, report.getHoursSpent(), Double::sum);
            }
        }
        return new UserMonthReport(userMonthReport.getUser(),
                userMonthReport.getIllnessDays(), userMonthReport.getVacationDays(), weeksWorkHours, filteredReports);
    }


}
