package com.easypark.reports.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import static com.easypark.reports.util.Constant.MAX_STRING_LENGTH;

public class StyleHelper {
    public static String setWrap(String value) {
        int length = value.length();
        if (length > MAX_STRING_LENGTH) {
            int index = value.indexOf(" ", MAX_STRING_LENGTH);
            if (index > 0) {
                value = value.substring(0, index) + "\n" + setWrap(value.substring(index + 1, value.length()));
            }
        }
        return value;
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
}
