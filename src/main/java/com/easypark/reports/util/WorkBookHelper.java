package com.easypark.reports.util;


import com.easypark.reports.entity.DevTimeTotal;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

import static com.easypark.reports.util.Constant.START_ROW_NUM;
import static com.easypark.reports.util.StyleHelper.setWrap;
import static com.easypark.reports.util.TotalHelper.getSumFormula;

public class WorkBookHelper {
    public static Row createOrdinaryRow(Sheet sheet, int startRowNum, List<String> values, List<CellStyle> styles) {
        Row row = sheet.createRow(startRowNum);
        row.setHeight((short) -1);
        int startCellNum = 1;
        int iterator = 0;
        for (String value : values) {
            Cell cell = row.createCell(startCellNum++);
            setCellValue(cell, value);
            cell.setCellStyle(styles.get(iterator));
            if (styles.size() > 1) {
                iterator++;
            }
        }
        for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
            sheet.autoSizeColumn(colNum);
        }
        return row;
    }

    public static List<String> getHeaders(int maxWeek) {
        if (maxWeek > 4) {
            return Lists.newArrayList("Developer", "Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Total");
        }
        return Lists.newArrayList("Developer", "Week 1", "Week 2", "Week 3", "Week 4", "Total");
    }

    public static List<String> getTableData(int maxWeek, DevTimeTotal timeTotal) {
        List<String> tableData = Lists.newArrayList(NameCreator.createNameFromKey(timeTotal.getDeveloperName()));
        maxWeek = maxWeek + 1;
        for (int i = 1; i < maxWeek; i++) {
            tableData.add(String.valueOf(timeTotal.getWeeks().getOrDefault(i, 0.)));
        }
        return tableData;
    }

    public static List<String> getTotal(Sheet sheet, int maxWeek) {
        int lastRow = sheet.getPhysicalNumberOfRows() + START_ROW_NUM;
        List<String> total = Lists.newArrayList("Total");
        maxWeek = maxWeek + 2;
        for (int i = 2; i < maxWeek; i++) {
            total.add(getSumFormula(sheet, i, i, START_ROW_NUM + 1, lastRow));
        }
        return total;
    }

    private static void setCellValue(Cell cell, String value) {
        if (value.matches("^(%reportsTotalFormula_.*)$")) {
            cell.setCellType(CellType.FORMULA);
            cell.setCellFormula(value.split("_")[1]);
            return;
        }
        if (isDouble(value)) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(new Double(value));
            return;
        }
        value = setWrap(value);
        cell.setCellValue(value);
    }

    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
