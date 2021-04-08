package com.easypark.reports.util;

import com.easypark.reports.entity.Report;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

import static com.easypark.reports.util.Constant.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupSheetUtils {

    public static int addHeaderRow(int rowIndex, Sheet sheet) {
        int startCellIndex = START_CELL_NUM;
        Row row = sheet.createRow(rowIndex);
        CellStyle coloredBorderedStyle = StylesUtils.getColoredBordered(sheet.getWorkbook(), HorizontalAlignment.CENTER);
        coloredBorderedStyle.setFont(StylesUtils.getBold(sheet.getWorkbook()));
        for (String header : WORK_REPORTS_HEADERS) {
            addCell(startCellIndex++, row, header, coloredBorderedStyle);
        }
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

    public static int addRow(int rowIndex, Report report, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int startCellNum = START_CELL_NUM;
        CellStyle borderedOnCenter = StylesUtils.getBordered(sheet.getWorkbook(), HorizontalAlignment.CENTER);
        CellStyle borderedOnLeft = StylesUtils.getBordered(sheet.getWorkbook(), HorizontalAlignment.LEFT);
        addCell(startCellNum++, row, report.getStarted().toString(), borderedOnCenter);
        addCell(startCellNum++, row, report.getIssueKey(), borderedOnCenter);
        addCell(startCellNum++, row, setWrap(report.getSummary()), borderedOnLeft);
        addCell(startCellNum++, row, report.getHoursSpent(), borderedOnCenter);
        addCell(startCellNum, row, setWrap(report.getComment()), borderedOnLeft);
        return row.getRowNum();
    }

    public static Row addWeekTotalRow(int rowIndex, int weekStartIndex, Sheet sheet, int week) {
        Row row = sheet.createRow(rowIndex);
        int startCellNum = START_CELL_NUM;
        CellStyle coloredBordered = StylesUtils.getColoredBordered(sheet.getWorkbook(), HorizontalAlignment.CENTER);
        coloredBordered.setFont(StylesUtils.getBold(sheet.getWorkbook()));
        addCell(startCellNum++, row, "Week " + week, coloredBordered);
        addCell(startCellNum++, row, " ", coloredBordered);
        addCell(startCellNum++, row, " ", coloredBordered);
        String startWeekAddress = sheet.getRow(weekStartIndex).getCell(4).getAddress().formatAsString();
        String endWeekAddress = sheet.getRow(rowIndex - 1).getCell(4).getAddress().formatAsString();
        SheetUtils.addCellFormula(startCellNum++, startWeekAddress, endWeekAddress, row);
        addCell(startCellNum, row, " ", coloredBordered);
        return row;
    }

    public static void addMonthTotalRow(int rowIndex, List<String> weekTotalsIndexes, Sheet sheet) {
        Row row = sheet.createRow(rowIndex);
        int startCellNum = START_CELL_NUM;
        CellStyle coloredBordered = StylesUtils.getColoredBordered(sheet.getWorkbook(), HorizontalAlignment.CENTER);
        coloredBordered.setFont(StylesUtils.getBold(sheet.getWorkbook()));
        addCell(startCellNum++, row, "Total", coloredBordered);
        addCell(startCellNum++, row, " ", coloredBordered);
        addCell(startCellNum++, row, " ", coloredBordered);

        addCellFormula(startCellNum++, weekTotalsIndexes, row);
        addCell(startCellNum, row, " ", coloredBordered);
    }

    private static void addCellFormula(int cellIndex, List<String> weekTotalsIndexes, Row row) {
        Cell cell = row.createCell(cellIndex);
        CellStyle style = StylesUtils.getColoredBordered(row.getSheet().getWorkbook(), HorizontalAlignment.CENTER);
        style.setFont(StylesUtils.getBold(row.getSheet().getWorkbook()));
        style.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(style);
        cell.setCellFormula(String.format(SUM_FORMULA_TEMPLATE, String.join(", ", weekTotalsIndexes)));
    }

    private static String setWrap(String value) {
        int length = value.length();
        if (length > MAX_STRING_LENGTH) {
            int index = value.indexOf(" ", MAX_STRING_LENGTH);
            if (index > 0) {
                value = value.substring(0, index) + "\n" + setWrap(value.substring(index + 1));
            }
        }
        return value;
    }
}
