package com.easypark.reports.util;

import java.util.List;

public class Constant {
    public static final int START_ROW_NUM = 3;
    public static final int START_CELL_NUM = 1;
    public static final int MAX_STRING_LENGTH = 70;
    public static final List<String> WORK_REPORTS_HEADERS = List.of("Date Started", "Key", "Name", "Time Spent (h)", "Work Description");
    public static final List<String> TOTAL_HEADERS = List.of("Developer", "Month");
    public static final List<String> ILLNESS_HEADERS = List.of("Developer", "Illness (d)");
    public static final List<String> VACATION_HEADERS = List.of("Developer", "Vacation (d)");
    public static final String SUMMARY_SHEET_NAME = "Summary";
    public static final String TOTAL_SHEET_NAME = "Total";
    public static final String SUM_FORMULA_TEMPLATE = "SUM(%s)";
    public static final String NUMERIC_FORMAT = "#.###";
    public static final double ROUNDING_TO_DIGIT = 100.00;//10^(number of digits after coma)
}
