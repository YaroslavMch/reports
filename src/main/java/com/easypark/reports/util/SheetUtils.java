package com.easypark.reports.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

import java.util.Iterator;

import static com.easypark.reports.util.Constant.SUM_FORMULA_TEMPLATE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SheetUtils {

    public static void addCellFormula(int cellIndex, String firstCell, String lastCell, Row row) {
        Cell cell = row.createCell(cellIndex);
        CellStyle style = StylesUtils.getColoredBordered(row.getSheet().getWorkbook(), HorizontalAlignment.CENTER);
        style.setFont(StylesUtils.getBold(row.getSheet().getWorkbook()));
        style.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(style);
        cell.setCellFormula(String.format(SUM_FORMULA_TEMPLATE, firstCell + ":" + lastCell));
    }

    public static void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                autoSizeRows(sheet);
            }
        }
    }

    private static void autoSizeRows(Sheet sheet) {
        Row row = sheet.getRow(sheet.getFirstRowNum());
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            autoSizeCell(cellIterator, sheet);
        }
    }

    private static void autoSizeCell(Iterator<Cell> cellIterator, Sheet sheet) {
        Cell cell = cellIterator.next();
        int columnIndex = cell.getColumnIndex();
        sheet.autoSizeColumn(columnIndex);
    }
}