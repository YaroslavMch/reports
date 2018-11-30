package com.easypark.reports.workbook;

import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.DevGroup;
import com.easypark.reports.entity.DevTimeTotal;
import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.util.GroupHelper;
import com.easypark.reports.util.NameCreator;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.easypark.reports.util.Constant.START_ROW_NUM;
import static com.easypark.reports.util.StyleHelper.createBorder;
import static com.easypark.reports.util.StyleHelper.createStyleForColorCell;
import static com.easypark.reports.util.TotalHelper.getDevTimes;
import static com.easypark.reports.util.TotalHelper.getSumFormula;
import static com.easypark.reports.util.TotalHelper.getSumMonthFormula;
import static com.easypark.reports.util.WorkBookHelper.createOrdinaryRow;
import static com.easypark.reports.util.WorkBookHelper.getHeaders;
import static com.easypark.reports.util.WorkBookHelper.getTableData;
import static com.easypark.reports.util.WorkBookHelper.getTotal;
import static java.util.Collections.singletonList;

@Slf4j
@Getter
public class DevReports implements Runnable {
    private static final String SUMMARY_SHEET_NAME = "Summary";
    private final Map<String, List<TimeReport>> timeReports;
    private final TimeReportProperties timeReportProperties;
    private List<GroupWorkBook> workBooks;

    public DevReports(Map<String, List<TimeReport>> timeReports, TimeReportProperties timeReportProperties) {
        this.timeReports = timeReports;
        this.timeReportProperties = timeReportProperties;
    }

    @Override
    public void run() {
        getTimeReportTable(timeReports, timeReportProperties);
    }

    private void getTimeReportTable(Map<String, List<TimeReport>> timeReports,
                                    TimeReportProperties timeReportProperties) {
        this.workBooks = Arrays.stream(DevGroup.values())
                .parallel()
                .map(devGroup -> new GroupWorkBook(devGroup.getName(),
                        createTable(timeReports, GroupHelper.getGroup(timeReportProperties, devGroup), devGroup.getRegex())
                )).collect(Collectors.toList());
    }

    private Workbook createTable(Map<String, List<TimeReport>> timeReports,
                                 List<String> allKeys, String regex) {
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
        List<CellStyle> styles = Lists.newArrayList(colorStyle, styleAlignCenter, styleAlignLeft);
        List<DevTimeTotal> devTimeTotals = Lists.newArrayList();
        int weekInMonth;
        int maxWeek = 0;
        for (String key : allKeys) {
            if (timeReports.containsKey(key)) {
                List<TimeReport> reports = timeReports.get(key)
                        .parallelStream()
                        .filter(timeReport -> timeReport.getTaskKey().matches(regex))
                        .collect(Collectors.toList());
                devTimeTotals.add(getDevTimes(reports, key));
                if (!reports.isEmpty()) {
                    weekInMonth = createRows(workBook.createSheet(NameCreator.createNameFromKey(key)), reports, styles);
                    if (weekInMonth > maxWeek) {
                        maxWeek = weekInMonth;
                    }
                }
            } else {
                log.info("Not found time reports for " + key);
            }
        }
        createSummarySheet(workBook.createSheet(SUMMARY_SHEET_NAME), devTimeTotals, styles, maxWeek);
        workBook.setSheetOrder(SUMMARY_SHEET_NAME, 0);
        return workBook;
    }

    private int createRows(Sheet sheet, List<TimeReport> timeReports, List<CellStyle> styles) {
        int startRow = START_ROW_NUM;
        int endRow = START_ROW_NUM;
        int weekInMonth = 1;
        int weekCounter = 1;
        List<Integer> weekCellNum = Lists.newArrayList();
        createOrdinaryRow(sheet, 3,
                Lists.newArrayList("Date Started", "Key", "Name", "Time Spent (h)", "Work Description"), singletonList(styles.get(0)));
        for (TimeReport timeReport : timeReports) {
            int currentWeek = timeReport.getDate().get(WeekFields.ISO.weekOfMonth());
            if (currentWeek > weekCounter) {
                if (endRow > 4) {
                    String formula = getSumFormula(sheet, 4, 4, startRow, endRow);
                    weekCellNum.add(endRow);
                    createOrdinaryRow(sheet, endRow++,
                            Lists.newArrayList("Week " + weekCounter, "", "", formula, ""), singletonList(styles.get(0)));
                    startRow = endRow;
                }
                weekCounter = currentWeek;
                if (weekCounter > weekInMonth) {
                    weekInMonth = weekCounter;
                }
            }
            createOrdinaryRow(sheet, endRow++,
                    Lists.newArrayList(timeReport.getDate().toString(), timeReport.getTaskKey(),
                            timeReport.getSummary(), String.valueOf(timeReport.getTimeSpent()), timeReport.getComment()),
                    Lists.newArrayList(styles.get(1), styles.get(1), styles.get(2), styles.get(1), styles.get(2)));
        }
        String weekFormula = getSumFormula(sheet, 4, 4, startRow, endRow);
        weekCellNum.add(endRow);
        createOrdinaryRow(sheet, endRow++, Lists.newArrayList("Week " + weekCounter++, "", "",
                weekFormula, ""), singletonList(styles.get(0)));
        String monthFormula = getSumMonthFormula(sheet, 4, weekCellNum);
        createOrdinaryRow(sheet, endRow++, Lists.newArrayList("Total", "", "", monthFormula, ""),
                singletonList(styles.get(0)));
        return weekInMonth;
    }

    private void createSummarySheet(Sheet sheet, List<DevTimeTotal> devTimeTotals, List<CellStyle> styles, int weekInMonth) {
        int rowNum = START_ROW_NUM;
        createOrdinaryRow(sheet, rowNum++, getHeaders(weekInMonth)
                , singletonList(styles.get(0)));
        for (DevTimeTotal timeTotal : devTimeTotals) {
            createOrdinaryRow(sheet, rowNum++, getTableData(weekInMonth, timeTotal)
                    , Lists.newArrayList(styles.get(1), styles.get(1), styles.get(1),
                            styles.get(1), styles.get(1), styles.get(1), styles.get(0)));
        }
        createOrdinaryRow(sheet, rowNum++, getTotal(sheet, weekInMonth), singletonList(styles.get(0)));
        int cellNum = weekInMonth + 2;
        for (int i = START_ROW_NUM + 1; i < rowNum; i++) {
            Cell cell = sheet.getRow(i).createCell(cellNum);
            String formula = getSumFormula(sheet, 2, weekInMonth + 1, i, i);
            cell.setCellStyle(styles.get(0));
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(formula.split("_")[1]);
            sheet.setColumnWidth(cellNum, 4000);
            sheet.getRow(i).setHeight(sheet.getDefaultRowHeight());
        }
    }
}
