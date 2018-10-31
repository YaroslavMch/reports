package com.easypark.reports.util;


import com.easypark.reports.entity.DevTimeTotal;
import com.easypark.reports.service.TotalService;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class WorkBookHelper {
    public static final int START_ROW_NUM = 4;

    public static Row createOrdinaryRow(Sheet sheet, int startRowNum, List<String> values, List<CellStyle> styles) {
        Row row = sheet.createRow(startRowNum);
        int startCellNum = 1;
        int iterator = 0;
        for (String value : values) {
            Cell cell = row.createCell(startCellNum++);
            cell.setCellValue(value);
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

    public static void createStyleForColorCell(CellStyle style) {
        createBorder(style);
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
    }

    public static void createBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }

    public static List<String> getHeaders(int maxWeek) {
        if (maxWeek > 4) {
            return Lists.newArrayList("Developer", "Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Total");
        }
        return Lists.newArrayList("Developer", "Week 1", "Week 2", "Week 3", "Week 4", "Total");
    }

    public static List<String> getTableData(int maxWeek, DevTimeTotal timeTotal) {
        if (maxWeek > 4) {
            return Lists.newArrayList(
                    NameCreator.createNameFromKey(timeTotal.getDeveloperName()),
                    String.valueOf(timeTotal.getWeeks().getOrDefault(1, 0.)),
                    String.valueOf(timeTotal.getWeeks().getOrDefault(2, 0.)),
                    String.valueOf(timeTotal.getWeeks().getOrDefault(3, 0.)),
                    String.valueOf(timeTotal.getWeeks().getOrDefault(4, 0.)),
                    String.valueOf(timeTotal.getWeeks().getOrDefault(5, 0.)),
                    String.valueOf(timeTotal.getTotal()));
        }
        return Lists.newArrayList(
                NameCreator.createNameFromKey(timeTotal.getDeveloperName()),
                String.valueOf(timeTotal.getWeeks().getOrDefault(1, 0.)),
                String.valueOf(timeTotal.getWeeks().getOrDefault(2, 0.)),
                String.valueOf(timeTotal.getWeeks().getOrDefault(3, 0.)),
                String.valueOf(timeTotal.getWeeks().getOrDefault(4, 0.)),
                String.valueOf(timeTotal.getTotal()));
    }

    public static List<String> getTotal(Sheet sheet, int maxWeek, TotalService totalService) {
        if (maxWeek > 4) {
            return Lists.newArrayList("Total",
                    String.valueOf(totalService.countTotal(sheet, 2)),
                    String.valueOf(totalService.countTotal(sheet, 3)),
                    String.valueOf(totalService.countTotal(sheet, 4)),
                    String.valueOf(totalService.countTotal(sheet, 5)),
                    String.valueOf(totalService.countTotal(sheet, 6)),
                    String.valueOf(totalService.countTotal(sheet, 7)));
        }
        return Lists.newArrayList("Total",
                String.valueOf(totalService.countTotal(sheet, 2)),
                String.valueOf(totalService.countTotal(sheet, 3)),
                String.valueOf(totalService.countTotal(sheet, 4)),
                String.valueOf(totalService.countTotal(sheet, 5)),
                String.valueOf(totalService.countTotal(sheet, 6)));
    }
}
