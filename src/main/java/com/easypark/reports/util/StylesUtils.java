package com.easypark.reports.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StylesUtils {

    public static CellStyle getColoredBordered(Workbook workbook, HorizontalAlignment alignment) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setAlignment(alignment);
        cellStyle.setWrapText(true);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        return cellStyle;
    }

    public static Font getBold(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        return font;
    }

    public static CellStyle getBordered(Workbook workbook, HorizontalAlignment alignment) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(alignment);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        return cellStyle;
    }
}
