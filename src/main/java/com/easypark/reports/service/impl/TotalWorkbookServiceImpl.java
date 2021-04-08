package com.easypark.reports.service.impl;

import com.easypark.reports.entity.GroupWorkbook;
import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.entity.UserMonthReport;
import com.easypark.reports.service.TotalWorkbookService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import static com.easypark.reports.util.Constant.*;
import static com.easypark.reports.util.SheetUtils.autoSizeColumns;
import static com.easypark.reports.util.TotalSheetUtils.*;

@Service
public class TotalWorkbookServiceImpl implements TotalWorkbookService {

    public GroupWorkbook createTotalWorkbook(MonthReport monthReport) {
        int rowNum = START_ROW_NUM;
        Workbook workbook = new XSSFWorkbook();
        Sheet totalSheet = workbook.createSheet(TOTAL_SHEET_NAME);
        rowNum = writeReportsHours(monthReport, rowNum, totalSheet) + 3;
        rowNum = writeVacationReports(monthReport, rowNum, totalSheet) + 3;
        writeIllnessReports(monthReport, rowNum, totalSheet);
        autoSizeColumns(workbook);
        return new GroupWorkbook(TOTAL_SHEET_NAME, workbook);
    }

    private void writeIllnessReports(MonthReport monthReport, int rowNum, Sheet totalSheet) {
        int startRow = rowNum;
        startRow = addHeaderRow(startRow, totalSheet, ILLNESS_HEADERS) + 1;
        for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
            if (userMonthReport.getIllnessDays() > 0) {
                startRow = addIllnessRow(startRow, totalSheet, userMonthReport) + 1;
            }
        }
    }

    private int writeVacationReports(MonthReport monthReport, int rowNum, Sheet totalSheet) {
        int startRow = rowNum;
        startRow = addHeaderRow(startRow, totalSheet, VACATION_HEADERS) + 1;
        for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
            if (userMonthReport.getVacationDays() > 0) {
                startRow = addVacationRow(startRow, totalSheet, userMonthReport) + 1;
            }
        }
        return startRow;
    }

    private int writeReportsHours(MonthReport monthReport, int rowNum, Sheet totalSheet) {
        int startRow = rowNum;
        startRow = addHeaderRow(startRow, totalSheet, TOTAL_HEADERS) + 1;
        int firstHourRowIndex = startRow;
        for (UserMonthReport userMonthReport : monthReport.getUsersReports()) {
            startRow = addRow(startRow, totalSheet, userMonthReport) + 1;
        }
        return addTotalRow(startRow, totalSheet, firstHourRowIndex) + 1;
    }

}
