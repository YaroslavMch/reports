package com.easypark.reports.util;

import com.easypark.reports.entity.UserMonthReport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.List;

import static com.easypark.reports.util.Constant.START_CELL_NUM;
import static com.easypark.reports.util.SheetUtils.addCellFormula;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SummarySheetUtils {

    public static int addHeaderRow(int rowIndex, Sheet sheet, int numberOfWeeks) {
        int startCellIndex = START_CELL_NUM;
        Row row = sheet.createRow(rowIndex);
        CellStyle coloredBorderedStyle = StylesUtils.getColoredBordered(sheet.getWorkbook(), HorizontalAlignment.CENTER);
        coloredBorderedStyle.setFont(StylesUtils.getBold(sheet.getWorkbook()));
        for (String header : createSummaryHeader(numberOfWeeks)) {
            addCell(startCellIndex++, row, header, coloredBorderedStyle);
        }
        return row.getRowNum();
    }

    public static int addRow(int rowIndex, Sheet sheet, UserMonthReport userMonthReport, int numOfWeeks) {
        Row row = sheet.createRow(rowIndex);
        int startCellIndex = START_CELL_NUM;
        CellStyle borderedStyle = StylesUtils.getBordered(row.getSheet().getWorkbook(), HorizontalAlignment.CENTER);
        addCell(startCellIndex++, row, userMonthReport.getUser().getDisplayName(), borderedStyle);
        for (int i = 1; i <= numOfWeeks; i++) {
            addCell(startCellIndex++, row, userMonthReport.getWeeksHours().getOrDefault(i, 0.0), borderedStyle);
        }
        String firstWeekAddress = row.getCell(row.getFirstCellNum() + 1).getAddress().formatAsString();
        String lastWeekAddress = row.getCell(row.getFirstCellNum() + numOfWeeks).getAddress().formatAsString();
        addCellFormula(startCellIndex, firstWeekAddress, lastWeekAddress, row);
        return row.getRowNum();
    }

    private static void addCell(int cellIndex, Row row, String value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    private static void addCell(int cellIndex, Row row, double value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    public static void addTotalRow(int rowIndex, Sheet sheet, int numOfWeeks) {
        Row row = sheet.createRow(rowIndex);
        int startCellIndex = START_CELL_NUM;
        CellStyle coloredBordered = StylesUtils.getColoredBordered(sheet.getWorkbook(), HorizontalAlignment.CENTER);
        coloredBordered.setFont(StylesUtils.getBold(sheet.getWorkbook()));
        addCell(startCellIndex++, row, "Total", coloredBordered);
        for (int i = 1; i <= numOfWeeks + 1; i++) {
            String firstCellAddress = sheet.getRow(sheet.getFirstRowNum() + 1).getCell(i + 1).getAddress().formatAsString();
            String lastCellAddress = sheet.getRow(sheet.getLastRowNum() - 1).getCell(i + 1).getAddress().formatAsString();
            SheetUtils.addCellFormula(startCellIndex++, firstCellAddress, lastCellAddress, row);
        }
    }

    public static List<String> createSummaryHeader(int numberOfWeeks) {
        List<String> cells = new ArrayList<>();
        cells.add("Developer");
        for (int i = 1; i <= numberOfWeeks; i++) {
            cells.add("Week " + i);
        }
        cells.add("     Total     ");
        return cells;
    }
}
