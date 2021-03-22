package com.easypark.reports.util;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class TotalHelper {

    public static String getSumFormula(Sheet sheet, int startCellNum, int endCellNum, int startRow, int endRow) {
        if (endRow != startRow) {
            endRow--;
        }
        String cellStart = sheet.getRow(startRow).getCell(startCellNum).getAddress().formatAsString();
        String cellEnd = sheet.getRow(endRow).getCell(endCellNum).getAddress().formatAsString();
        return Constant.FORMULA_PREFIX + "SUM(" + cellStart + ":" + cellEnd + ")";
    }

    public static String getSumMonthFormula(Sheet sheet, int cellNum, List<Integer> weekCellNum) {
        String formula = Constant.FORMULA_PREFIX + "SUM(";
        for (int num : weekCellNum) {
            formula += sheet.getRow(num).getCell(cellNum).getAddress().formatAsString() + ",";
        }
        return formula.substring(0, formula.length() - 1) + ")";
    }
}