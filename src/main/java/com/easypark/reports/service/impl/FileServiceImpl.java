package com.easypark.reports.service.impl;

import com.easypark.reports.entity.Month;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.service.FileService;
import com.easypark.reports.service.TimeReportService;
import com.easypark.reports.util.MonthParser;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private final TimeReportService timeReportService;

    @Override
    public Workbook getTimeReportTable(String month, Integer year, HttpServletResponse response) {
        if (Objects.isNull(year)) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }
        Month monthRange = MonthParser.getMonthRange(month, year, response);
        Map<String, List<TimeReport>> timeReports = timeReportService.getGroupedTimeReports(monthRange);
        List<String> allKeys = timeReportService.getAllTimeReportKeys(timeReports);
        return createTable(timeReports, allKeys);
    }

    private Workbook createTable(Map<String, List<TimeReport>> timeReports, List<String> allKeys) {
        Workbook workBook = new XSSFWorkbook();
        CellStyle color_style = workBook.createCellStyle();
        CellStyle style_align_center = workBook.createCellStyle();
        CellStyle style_no_align = workBook.createCellStyle();
        createStyleForColorCell(color_style);
        createBorder(style_align_center);
        createBorder(style_no_align);
        style_align_center.setAlignment(HorizontalAlignment.CENTER);
        List<CellStyle> styles = Lists.newArrayList(color_style, style_align_center, style_no_align);
        allKeys.stream().forEach(key -> createRow(workBook.createSheet(key),
                timeReports.get(key), styles));
        return workBook;
    }

    private void createRow(Sheet sheet, List<TimeReport> timeReports, List<CellStyle> styles) {
        int rowNum = 4;
        double timeInWeek = 0;
        double timeInMonth = 0;
        int weekCounter = 1;
        createColorRow(sheet, styles.get(0), 3, "Key", "Date Started", "Time Spent (h)", "Work Description");
        for (TimeReport timeReport : timeReports) {
            int currentWeek = timeReport.getDate().get(WeekFields.ISO.weekOfMonth());
            if (currentWeek > weekCounter) {
                timeInMonth += timeInWeek;
                createColorRow(sheet, styles.get(0), rowNum++, "Week " + weekCounter, "", String.valueOf(timeInWeek), "");
                timeInWeek = 0;
                weekCounter = currentWeek;
            }
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(1);
            cell.setCellValue(timeReport.getTaskKey());
            cell.setCellStyle(styles.get(1));
            Cell cel2 = row.createCell(2);
            cel2.setCellValue(timeReport.getDate().toString());
            cel2.setCellStyle(styles.get(1));
            Cell cel3 = row.createCell(3);
            cel3.setCellValue(timeReport.getTimeSpent());
            cel3.setCellStyle(styles.get(1));
            Cell cel4 = row.createCell(4);
            cel4.setCellValue(timeReport.getComment());
            cel4.setCellStyle(styles.get(2));
            timeInWeek += timeReport.getTimeSpent();
            for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                sheet.autoSizeColumn(colNum);
            }
        }
        timeInMonth += timeInWeek;
        createColorRow(sheet, styles.get(0), rowNum++, "Week " + weekCounter++, "", String.valueOf(timeInWeek), "");
        createColorRow(sheet, styles.get(0), rowNum++, "Total", "", String.valueOf(timeInMonth), "");
    }

    private void createColorRow(Sheet sheet, CellStyle style, int rowIndex,
                                String value1, String value2, String value3, String value4) {
        Row row = sheet.createRow(rowIndex);
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(value1);
        cell1.setCellStyle(style);
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(value2);
        cell2.setCellStyle(style);
        Cell cell3 = row.createCell(3);
        cell3.setCellValue(value3);
        cell3.setCellStyle(style);
        Cell cell4 = row.createCell(4);
        cell4.setCellValue(value4);
        cell4.setCellStyle(style);
    }

    private void createStyleForColorCell(CellStyle style) {
        createBorder(style);
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
    }

    private void createBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }
}
