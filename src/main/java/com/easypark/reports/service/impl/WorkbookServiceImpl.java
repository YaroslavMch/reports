package com.easypark.reports.service.impl;

import com.easypark.reports.properties.JiraProperties;
import com.easypark.reports.entity.*;
import com.easypark.reports.service.MonthReportService;
import com.easypark.reports.service.WorkbookService;
import com.easypark.reports.service.UserService;
import com.easypark.reports.util.NameCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static com.easypark.reports.util.Constant.START_ROW_NUM;
import static com.easypark.reports.util.StyleHelper.createBorder;
import static com.easypark.reports.util.StyleHelper.createStyleForColorCell;
import static com.easypark.reports.util.TotalHelper.getSumFormula;
import static com.easypark.reports.util.TotalHelper.getSumMonthFormula;
import static com.easypark.reports.util.WorkBookHelper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkbookServiceImpl implements WorkbookService {
    private static final String SUMMARY_SHEET_NAME = "Summary";

    private final UserService userService;
    private final MonthReportService monthReportService;

    public List<GroupWorkBook> createUsersWorkbooks(MonthReport monthReport) {
        List<GroupWorkBook> workBooks = Arrays.stream(DevGroup.values())
                .parallel()
                .map(devGroup -> new GroupWorkBook(devGroup.getName(),
                        createTable(userService.getGroup(devGroup), devGroup.getRegex(), monthReport)
                )).collect(Collectors.toList());
        GroupWorkBook total = new GroupWorkBook("Total", createTotalWorkBook(monthReport));
        workBooks.add(total);
        return workBooks;
    }

    private Workbook createTotalWorkBook(MonthReport monthReport) {
        Workbook workBook = new XSSFWorkbook();
        Font font = workBook.createFont();
        font.setBold(true);
        CellStyle colorCell = workBook.createCellStyle();
        colorCell.setFont(font);
        createBorder(colorCell);
        createStyleForColorCell(colorCell);
        CellStyle styleAlignCenter = workBook.createCellStyle();
        createBorder(styleAlignCenter);
        styleAlignCenter.setAlignment(HorizontalAlignment.CENTER);
        Sheet totalSheet = workBook.createSheet("Total");
        writeReportsToTotalSheet(monthReport, colorCell, styleAlignCenter, totalSheet);
        return workBook;
    }

    private void writeReportsToTotalSheet(MonthReport monthReport, CellStyle colorCell, CellStyle styleAlignCenter, Sheet totalSheet) {
        double monthTotal = 0.0;
        int rowNum = START_ROW_NUM;
        createOrdinaryRow(totalSheet, rowNum++, List.of("Developer", "Month total"), List.of(colorCell));
        for (UserMonthReport report: monthReport.getUsersReports()) {
            createOrdinaryRow(totalSheet, rowNum++, List.of(NameCreator.createNameFromKey(report.getDeveloperName()),
                    String.valueOf(report.getWorkHours())), List.of(styleAlignCenter));
            monthTotal += report.getWorkHours();
        }
        createOrdinaryRow(totalSheet, rowNum, List.of("Total", String.valueOf(monthTotal)), List.of(colorCell));
        if (!monthReport.getUsersIllnessDays().isEmpty()) {
            rowNum += START_ROW_NUM;
            createOrdinaryRow(totalSheet, rowNum++, List.of("Developer", "Month illness total (days)"), List.of(colorCell));
            for (String name: monthReport.getUsersIllnessDays().keySet()) {
                createOrdinaryRow(totalSheet, rowNum, List.of(NameCreator.createNameFromKey(name),
                        String.valueOf(monthReport.getUsersIllnessDays().get(name))), List.of(styleAlignCenter));
            }
        }
        if (!monthReport.getUsersVacationDays().isEmpty()) {
            rowNum += START_ROW_NUM;
            createOrdinaryRow(totalSheet, rowNum++, List.of("Developer", "Month vacation total (days)"), List.of(colorCell));
            for (String name: monthReport.getUsersVacationDays().keySet()) {
                createOrdinaryRow(totalSheet, rowNum, List.of(NameCreator.createNameFromKey(name),
                        String.valueOf(monthReport.getUsersVacationDays().get(name))), List.of(styleAlignCenter));
            }
        }
    }

    private Workbook createTable(List<String> users, String regex, MonthReport monthReport) {
        Workbook workBook = new XSSFWorkbook();
        Font font = workBook.createFont();
        font.setBold(true);
        CellStyle colorStyle = workBook.createCellStyle();
        colorStyle.setFont(font);
        CellStyle styleAlignCenter = workBook.createCellStyle();
        CellStyle styleAlignLeft = workBook.createCellStyle();
        createStyleForColorCell(colorStyle);
        createBorder(styleAlignCenter);
        createBorder(styleAlignLeft);
        colorStyle.setWrapText(true);
        styleAlignCenter.setWrapText(true);
        styleAlignLeft.setWrapText(true);
        styleAlignLeft.setAlignment(HorizontalAlignment.LEFT);
        styleAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        styleAlignCenter.setAlignment(HorizontalAlignment.CENTER);
        styleAlignCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        writeReports(users, regex, workBook, colorStyle, styleAlignCenter, styleAlignLeft, monthReport);
        return workBook;
    }

    private void writeReports(List<String> users, String regex, Workbook workBook,
                              CellStyle colorStyle, CellStyle styleAlignCenter, CellStyle styleAlignLeft, MonthReport monthReport) {
        List<CellStyle> styles = List.of(colorStyle, styleAlignCenter, styleAlignLeft);
        for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
            if (users.contains(userMonthReport.getDeveloperName())) {
                List<TimeReport> reports = filterReports(userMonthReport.getReports(), regex);
                if (!reports.isEmpty()) {
                    createRows(workBook.createSheet(NameCreator.createNameFromKey(userMonthReport.getDeveloperName())), reports, styles);
                } else {
                    log.info("Not found time reports for " + userMonthReport.getDeveloperName());
                }
            }
        }
        createSummarySheet(workBook.createSheet(SUMMARY_SHEET_NAME), monthReport, styles, users, regex);
        workBook.setSheetOrder(SUMMARY_SHEET_NAME, 0);
    }

    private List<TimeReport> filterReports(List<TimeReport> reports, String regex) {
        return reports
                .parallelStream()
                .filter(timeReport -> timeReport.getIssueKey().matches(regex))
                .collect(Collectors.toUnmodifiableList());
    }

    private int createRows(Sheet sheet, List<TimeReport> timeReports, List<CellStyle> styles) {
        int startRow = START_ROW_NUM;
        int endRow = START_ROW_NUM;
        int weekInMonth = 1;
        int weekCounter = 1;
        List<Integer> weekCellNum = new ArrayList<>();
        createOrdinaryRow(sheet, 3,
                List.of("Date Started", "Key", "Name", "Time Spent (h)", "Work Description"), List.of(styles.get(0)));
        for (TimeReport timeReport : timeReports) {
            int currentWeek = timeReport.getStarted().get(WeekFields.ISO.weekOfMonth());
            if (currentWeek > weekCounter) {
                if (endRow > 4) {
                    String formula = getSumFormula(sheet, 4, 4, startRow, endRow);
                    weekCellNum.add(endRow);
                    createOrdinaryRow(sheet, endRow++, List.of("Week " + weekCounter, "", "", formula, ""), List.of(styles.get(0)));
                    startRow = endRow;
                }
                weekCounter = currentWeek;
                if (weekCounter > weekInMonth) {
                    weekInMonth = weekCounter;
                }
            }
            createOrdinaryRow(sheet, endRow++,
                    List.of(timeReport.getStarted().toString(), timeReport.getIssueKey(),
                            timeReport.getSummary(), String.valueOf(timeReport.getHoursSpent()), timeReport.getComment()),
                    List.of(styles.get(1), styles.get(1), styles.get(2), styles.get(1), styles.get(2)));
        }
        String weekFormula = getSumFormula(sheet, 4, 4, startRow, endRow);
        weekCellNum.add(endRow);
        createOrdinaryRow(sheet, endRow++, List.of("Week " + weekCounter, "", "", weekFormula, ""), List.of(styles.get(0)));
        String monthFormula = getSumMonthFormula(sheet, 4, weekCellNum);
        createOrdinaryRow(sheet, endRow, List.of("Total", "", "", monthFormula, ""), List.of(styles.get(0)));
        return weekInMonth;
    }

    private void createSummarySheet(Sheet sheet, MonthReport monthReport, List<CellStyle> styles, List<String> users, String regex) {
        int rowNum = START_ROW_NUM;
        int numberOfWeeks = monthReport.getNumberOfWeeks();
        createOrdinaryRow(sheet, rowNum++, getHeaders(numberOfWeeks), List.of(styles.get(0)));
        for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
            if (users.contains(userMonthReport.getDeveloperName())) {
                UserMonthReport filteredUserMonthReport = monthReportService.validateUserMonthReport(userMonthReport, regex);
                    createOrdinaryRow(sheet, rowNum++, getTableData(numberOfWeeks, filteredUserMonthReport)
                            , List.of(styles.get(1), styles.get(1), styles.get(1), styles.get(1), styles.get(1), styles.get(1), styles.get(0)));

            }
        }
        createOrdinaryRow(sheet, rowNum++, getTotal(sheet, numberOfWeeks), List.of(styles.get(0)));
        int cellNum = numberOfWeeks + 2;
        for (int i = START_ROW_NUM + 1; i < rowNum; i++) {
            Cell cell = sheet.getRow(i).createCell(cellNum);
            String formula = getSumFormula(sheet, 2, numberOfWeeks + 1, i, i);
            cell.setCellStyle(styles.get(0));
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula.split("_")[1]);
            sheet.setColumnWidth(cellNum, 4000);
            sheet.getRow(i).setHeight(sheet.getDefaultRowHeight());
        }
    }

    public static List<String> getTableData(int maxWeek, UserMonthReport userMonthReport) {
        List<String> tableData = new ArrayList<>(List.of(NameCreator.createNameFromKey(userMonthReport.getDeveloperName())));
        maxWeek = maxWeek + 1;
        for (int i = 1; i < maxWeek; i++) {
            tableData.add(String.valueOf(userMonthReport.getWeeksHours().getOrDefault(i, 0.0)));
        }
        return tableData;
    }
}
