package com.easypark.reports.util;

import com.easypark.reports.entity.DevTimeTotal;
import com.easypark.reports.entity.TimeReport;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.temporal.WeekFields;
import java.util.List;

public class TotalHelper {

    public static DevTimeTotal getDevTimes(List<TimeReport> timeReports, String user) {
        int rowCounter = 0;
        double timeInWeek = 0;
        double timeInMonth = 0;
        int weekCounter = 1;
        DevTimeTotal timeTotal = new DevTimeTotal();
        timeTotal.setDeveloperName(user);
        for (TimeReport timeReport : timeReports) {
            int currentWeek = timeReport.getDate().get(WeekFields.ISO.weekOfMonth());
            if (currentWeek > weekCounter && rowCounter > 0) {
                timeInMonth += timeInWeek;
                timeTotal.putWeek(weekCounter, timeInWeek);
                timeInWeek = 0;
            }
            weekCounter = currentWeek;
            rowCounter++;
            timeInWeek += timeReport.getTimeSpent();
        }
        timeInMonth += timeInWeek;
        timeTotal.putWeek(weekCounter, timeInWeek);
        timeTotal.setTotal(timeInMonth);
        return timeTotal;
    }

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
