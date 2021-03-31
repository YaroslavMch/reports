package com.easypark.reports.service.impl;

import com.easypark.reports.entity.*;
import com.easypark.reports.service.MonthReportService;
import com.easypark.reports.service.UserService;
import com.easypark.reports.service.WorkbookService;
import com.easypark.reports.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static com.easypark.reports.util.Constant.*;
import static com.easypark.reports.util.WorkbookUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkbookServiceImpl implements WorkbookService {
    private final UserService userService;
    private final MonthReportService monthReportService;

    public List<GroupWorkbook> createUsersWorkbooks(MonthReport monthReport) {
        List<GroupWorkbook> workbooks = Arrays.stream(UserGroup.values())
                .parallel()
                .map(group -> createGroupWorkbook(group, monthReport))
                .collect(Collectors.toList());
        workbooks.add(createTotalWorkbook(monthReport));
        return workbooks;
    }

    private GroupWorkbook createGroupWorkbook(UserGroup group, MonthReport monthReport) {
        int rowNum = START_ROW_NUM;
        Workbook workbook = new XSSFWorkbook();
        writeMonthSummary(group, monthReport, rowNum, workbook);
        rowNum = START_ROW_NUM;
        writeMonthReport(group, monthReport, rowNum, workbook);
        autoSizeColumns(workbook);
        return new GroupWorkbook(group.getName(), workbook);
    }

    private GroupWorkbook createTotalWorkbook(MonthReport monthReport) {
        final String name = "Total";
        int rowNum = START_ROW_NUM;
        Map<String, Double> illnessUsersDays = new HashMap<>();
        Map<String, Double> vacationUsersDays = new HashMap<>();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet totalSheet = workbook.createSheet(name);
        writeTotal(monthReport, rowNum, illnessUsersDays, vacationUsersDays, workbook, totalSheet);
        rowNum += START_ROW_NUM + monthReport.getUsersReports().size();
        writeVacationOrIllnessReports(rowNum, workbook, totalSheet, vacationUsersDays, VACATION_HEADERS);
        rowNum += START_ROW_NUM;
        writeVacationOrIllnessReports(rowNum, workbook, totalSheet, illnessUsersDays, ILLNESS_HEADERS);
        autoSizeColumns(workbook);
        return new GroupWorkbook(name, workbook);
    }

    private void writeTotal(MonthReport monthReport, int rowNum, Map<String, Double> illnessUsersDays, Map<String, Double> vacationUsersDays, XSSFWorkbook workbook, XSSFSheet totalSheet) {
        double monthHoursSum = 0;
        writeValues(workbook, totalSheet.createRow(rowNum++), TOTAL_HEADERS);
        for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
            writeTotal(userMonthReport.getUser().getDisplayName(), userMonthReport.getWorkHours(), workbook, totalSheet.createRow(rowNum++));
            monthHoursSum += userMonthReport.getWorkHours();
            if (userMonthReport.getIllnessDays() > 0) {
                illnessUsersDays.merge(userMonthReport.getUser().getDisplayName(), userMonthReport.getIllnessDays(), Double::sum);
            }
            if (userMonthReport.getVacationDays() > 0) {
                vacationUsersDays.merge(userMonthReport.getUser().getDisplayName(), userMonthReport.getVacationDays(), Double::sum);
            }
        }
        writeValues(workbook, totalSheet.createRow(rowNum), List.of("Total" , String.valueOf(monthHoursSum)));
    }

    private void writeVacationOrIllnessReports(int rowNum, Workbook workbook, Sheet totalSheet, Map<String, Double> usersDays, List<String> headers) {
        if (!usersDays.isEmpty()) {
            writeValues(workbook, totalSheet.createRow(rowNum++),  headers);
            for (String name : usersDays.keySet()) {
                writeTotal(name, usersDays.get(name), workbook, totalSheet.createRow(rowNum++));
            }
        }
    }

    private void writeTotal(String displayName, Double time, Workbook workbook, Row row) {
        int cellNum = START_CELL_NUM;
        CellStyle style = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false, false);
        createCell(cellNum++, row, displayName, style);
        createCell(cellNum, row, time, style);
    }

    private void writeMonthSummary(UserGroup group, MonthReport monthReport, int rowNum, Workbook workbook) {
        Sheet summarySheet = workbook.createSheet("Summary");
        int numberOfWeeks = DateUtils.getNumberOfWeeks(monthReport.getMonthRange());
        List<String> headers = createSummaryHeader(numberOfWeeks);
        writeValues(workbook, summarySheet.createRow(rowNum++), headers);
        List<User> users = userService.getGroup(group);
        Map<Integer, Double> weeksHoursSum = new HashMap<>();
        double usersMonthHoursSum = 0;
        for (User user : users) {
            for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
                if (user.getAccountId().equals(userMonthReport.getUser().getAccountId())) {
                    UserMonthReport filteredUserReport = monthReportService.filterUserMonthReport(userMonthReport, group);
                    for (Integer weekNum : filteredUserReport.getWeeksHours().keySet()) {
                        weeksHoursSum.merge(weekNum, filteredUserReport.getWeeksHours().get(weekNum), Double::sum);
                    }
                    usersMonthHoursSum += filteredUserReport.getWorkHours();
                    writeSummary(workbook, summarySheet.createRow(rowNum++), filteredUserReport, numberOfWeeks);
                }
            }
        }
        writeSummaryTotal(workbook, summarySheet.createRow(rowNum), createSummaryBottom(weeksHoursSum, usersMonthHoursSum));
    }

    private void writeSummaryTotal(Workbook workbook, Row row, List<Double> weeksSum) {
        int cellNum = START_CELL_NUM;
        CellStyle style = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true, true);
        createCell(cellNum++, row, "Total", style);
        for (Double weekSum : weeksSum) {
            createCell(cellNum++, row, weekSum, style);
        }
    }

    private void writeMonthReport(UserGroup group, MonthReport monthReport, int rowNum, Workbook workbook) {
        for (User user : userService.getGroup(group)) {
            for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
                if (user.getAccountId().equals(userMonthReport.getUser().getAccountId())) {
                    UserMonthReport filteredUserReport = monthReportService.filterUserMonthReport(userMonthReport, group);
                    if (!filteredUserReport.getReports().isEmpty()) {
                        writeUserReports(rowNum, workbook, filteredUserReport);
                    }
                }
            }
            rowNum = START_ROW_NUM;
        }
    }

    private void writeUserReports(int rowNum, Workbook workbook, UserMonthReport filteredUserReport) {
        Sheet userSheet = workbook.createSheet(filteredUserReport.getUser().getDisplayName());
        writeValues(workbook, userSheet.createRow(rowNum++), WORK_REPORTS_HEADERS);
        for (Integer weekNum : filteredUserReport.getWeeksHours().keySet()) {
            for (Report report : filteredUserReport.getReports()) {
                if (report.getStarted().get(WeekFields.ISO.weekOfMonth()) == weekNum) {
                    writeReport(workbook, userSheet.createRow(rowNum++), report);
                }
            }
            Double weekHours = filteredUserReport.getWeeksHours().get(weekNum);
            writeWeekTotal(workbook, userSheet.createRow(rowNum++), weekNum, weekHours);
        }
        writeMonthTotal(workbook, userSheet.createRow(rowNum), filteredUserReport.getWorkHours());
    }

    private void writeReport(Workbook workbook, Row row, Report report) {
        int cellNum = START_CELL_NUM;
        CellStyle alignCenterStyle = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false, false);
        CellStyle alignLeftStyle = createStyle(workbook, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false, false);
        createCell(cellNum++, row, report.getStarted().toString(), alignCenterStyle);
        createCell(cellNum++, row, report.getIssueKey(), alignCenterStyle);
        createCell(cellNum++, row, setWrap(report.getSummary()), alignLeftStyle);
        createCell(cellNum++, row, report.getHoursSpent(), alignCenterStyle);
        createCell(cellNum, row, setWrap(report.getComment()), alignLeftStyle);
    }

    private void writeSummary(Workbook workbook, Row row, UserMonthReport report, int numberOfWeeks) {
        int cellNum = START_CELL_NUM;
        CellStyle alignCenterStyle = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false, false);
        CellStyle alignCenterBoldStyle = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true, true);
        createCell(cellNum++, row, report.getUser().getDisplayName(), alignCenterStyle);
        for (int i = 1; i <= numberOfWeeks; i++) {
            createCell(cellNum++, row, report.getWeeksHours().getOrDefault(i, 0.0), alignCenterStyle);
        }
        createCell(cellNum, row, report.getWorkHours(), alignCenterBoldStyle);
    }

    private void writeValues(Workbook workbook, Row row, List<String> values) {
        int cellNum = START_CELL_NUM;
        CellStyle style = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true, true);
        for (String header : values) {
            createCell(cellNum++, row, header, style);
        }
    }

    private void writeWeekTotal(Workbook workbook, Row row, int weekNum, double weekHours) {
        int cellNum = START_CELL_NUM;
        CellStyle style = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true, true);
        createCell(cellNum++, row, "Week " + weekNum, style);
        createCell(cellNum++, row, " ", style);
        createCell(cellNum++, row, " ", style);
        createCell(cellNum++, row, weekHours, style);
        createCell(cellNum, row, " ", style);
    }

    private void writeMonthTotal(Workbook workbook, Row row, double monthHours) {
        int cellNum = START_CELL_NUM;
        CellStyle style = createStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true, true);
        createCell(cellNum++, row, "Total", style);
        createCell(cellNum++, row, " ", style);
        createCell(cellNum++, row, " ", style);
        createCell(cellNum++, row, monthHours, style);
        createCell(cellNum, row, " ", style);
    }
}
