package com.easypark.reports.workbook;

import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.DevGroup;
import com.easypark.reports.entity.DevTimeTotal;
import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.service.TotalService;
import com.easypark.reports.util.GroupHelper;
import com.easypark.reports.util.NameCreator;
import com.easypark.reports.util.WorkBookHelper;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.easypark.reports.util.WorkBookHelper.START_ROW_NUM;
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
    private final TotalService totalService;
    private List<GroupWorkBook> workBooks;

    public DevReports(Map<String, List<TimeReport>> timeReports, TimeReportProperties timeReportProperties, TotalService totalService) {
        this.timeReports = timeReports;
        this.timeReportProperties = timeReportProperties;
        this.totalService = totalService;
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
        CellStyle styleNoAlign = workBook.createCellStyle();
        WorkBookHelper.createStyleForColorCell(colorStyle);
        WorkBookHelper.createBorder(styleAlignCenter);
        WorkBookHelper.createBorder(styleNoAlign);
        styleAlignCenter.setAlignment(HorizontalAlignment.CENTER);
        List<CellStyle> styles = Lists.newArrayList(colorStyle, styleAlignCenter, styleNoAlign);
        List<DevTimeTotal> devTimeTotals = Lists.newArrayList();
        int weekInMonth;
        int maxWeek = 0;
        for (String key : allKeys) {
            if (timeReports.containsKey(key)) {
                List<TimeReport> reports = timeReports.get(key)
                        .parallelStream()
                        .filter(timeReport -> timeReport.getTaskKey().matches(regex))
                        .collect(Collectors.toList());
                devTimeTotals.add(totalService.getDevTimes(reports, key));
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
        int rowNum = 4;
        int weekInMonth = 1;
        double timeInWeek = 0;
        double timeInMonth = 0;
        int weekCounter = 1;
        createOrdinaryRow(sheet, 3,
                Lists.newArrayList("Key", "Date Started", "Time Spent (h)", "Work Description"), singletonList(styles.get(0)));
        for (TimeReport timeReport : timeReports) {
            int currentWeek = timeReport.getDate().get(WeekFields.ISO.weekOfMonth());
            if (currentWeek > weekCounter) {
                timeInMonth += timeInWeek;
                if (rowNum > 4) {
                    createOrdinaryRow(sheet, rowNum++,
                            Lists.newArrayList("Week " + weekCounter, "", String.valueOf(timeInWeek), ""), singletonList(styles.get(0)));
                }
                timeInWeek = 0;
                weekCounter = currentWeek;
                if (weekCounter > weekInMonth) {
                    weekInMonth = weekCounter;
                }
            }
            timeInWeek += timeReport.getTimeSpent();
            createOrdinaryRow(sheet, rowNum++,
                    Lists.newArrayList(timeReport.getTaskKey(), timeReport.getDate().toString(),
                            String.valueOf(timeReport.getTimeSpent()), timeReport.getComment()),
                    Lists.newArrayList(styles.get(1), styles.get(1), styles.get(1), styles.get(2)));
        }
        timeInMonth += timeInWeek;
        createOrdinaryRow(sheet, rowNum++, Lists.newArrayList("Week " + weekCounter++, "",
                String.valueOf(timeInWeek), ""), singletonList(styles.get(0)));
        createOrdinaryRow(sheet, rowNum++, Lists.newArrayList("Total", "", String.valueOf(timeInMonth), ""),
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
        createOrdinaryRow(sheet, rowNum++, getTotal(sheet, weekInMonth, totalService), singletonList(styles.get(0)));
    }
}
