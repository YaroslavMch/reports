package com.easypark.reports.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.easypark.reports.util.Constant.MAX_STRING_LENGTH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkbookUtils {


    public static Cell createCell(int index, Row row, String value, CellStyle cellStyle) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public static Cell createCell(int index, Row row, Double value, CellStyle cellStyle) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public static CellStyle createStyle(
            Workbook workbook, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, boolean bold, boolean colored
    ) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setAlignment(horizontalAlignment);
        cellStyle.setVerticalAlignment(verticalAlignment);
        cellStyle.setWrapText(true);
        if (colored) {
            cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (bold) {
            Font font = workbook.createFont();
            font.setBold(true);
            cellStyle.setFont(font);
        }
        return cellStyle;
    }

    public static List<String> createSummaryHeader(int numberOfWeeks) {
        List<String> cells = new ArrayList<>();
        cells.add("Developer");
        for (int i = 1; i <= numberOfWeeks; i++) {
            cells.add("Week " + i);
        }
        cells.add("Total");
        return cells;
    }

    public static List<Double> createSummaryBottom(Map<Integer, Double> weeksHoursSum, double monthHoursSum) {
        List<Double> cells = new ArrayList<>();
        for (Integer week : weeksHoursSum.keySet()) {
            cells.add(weeksHoursSum.get(week));
        }
        cells.add(monthHoursSum);
        return cells;
    }


    public static void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row row = sheet.getRow(sheet.getFirstRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }

    public static String setWrap(String value) {
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
