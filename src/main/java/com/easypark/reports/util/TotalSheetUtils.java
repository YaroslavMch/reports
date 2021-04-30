package com.easypark.reports.util;

import com.easypark.reports.entity.UserMonthReport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

import java.util.List;
import java.util.Map;

import static com.easypark.reports.util.Constant.START_CELL_NUM;
import static com.easypark.reports.util.StylesUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TotalSheetUtils {

    public static int addHeaderRow(int startRow, Sheet totalSheet, List<String> values) {
        int startCell = START_CELL_NUM;
        Row row = totalSheet.createRow(startRow);
        CellStyle coloredBordered = getColoredBordered(totalSheet.getWorkbook(), HorizontalAlignment.CENTER);
        coloredBordered.setFont(getBold(totalSheet.getWorkbook()));
        for (String value : values) {
            addCell(startCell++, row, value, coloredBordered);
        }
        return row.getRowNum();
    }

    public static int addVacationRow(int startRow, Sheet totalSheet, UserMonthReport userMonthReport) {
        int startCell = START_CELL_NUM;
        Row row = totalSheet.createRow(startRow);
        CellStyle bordered = getBordered(totalSheet.getWorkbook(), HorizontalAlignment.CENTER);
        addCell(startCell++, row, userMonthReport.getUser().getDisplayName(), bordered);
        addCell(startCell, row, userMonthReport.getVacationDays(), bordered);
        return row.getRowNum();
    }


    public static int addIllnessRow(int startRow, Sheet totalSheet, UserMonthReport userMonthReport) {
        int startCell = START_CELL_NUM;
        Row row = totalSheet.createRow(startRow);
        CellStyle bordered = getBordered(totalSheet.getWorkbook(), HorizontalAlignment.CENTER);
        addCell(startCell++, row, userMonthReport.getUser().getDisplayName(), bordered);
        addCell(startCell, row, userMonthReport.getIllnessDays(), bordered);
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

    public static int addRow(int startRow, Sheet totalSheet, UserMonthReport userMonthReport) {
        int startCell = START_CELL_NUM;
        Row row = totalSheet.createRow(startRow);
        CellStyle bordered = getBordered(totalSheet.getWorkbook(), HorizontalAlignment.CENTER);
        addCell(startCell++, row, userMonthReport.getUser().getDisplayName(), bordered);
        addCell(startCell, row, getWeeksSum(userMonthReport.getWeeksHours()), bordered);
        return row.getRowNum();
    }

    private static double getWeeksSum(Map<Integer, Double> weeksHours) {
        return weeksHours.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public static int addTotalRow(int startRow, Sheet totalSheet, int firstHourRowIndex) {
        int startCell = START_CELL_NUM;
        Row row = totalSheet.createRow(startRow);
        CellStyle coloredBordered = getColoredBordered(totalSheet.getWorkbook(), HorizontalAlignment.CENTER);
        coloredBordered.setFont(getBold(totalSheet.getWorkbook()));
        addCell(startCell++, row, "Total", coloredBordered);
        String firstCellAddress = totalSheet.getRow(firstHourRowIndex).getCell(2).getAddress().formatAsString();
        String lastCellAddress = totalSheet.getRow(startRow - 1).getCell(2).getAddress().formatAsString();
        SheetUtils.addCellFormula(startCell, firstCellAddress, lastCellAddress, row);
        return row.getRowNum();
    }
}
