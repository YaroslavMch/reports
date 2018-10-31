package com.easypark.reports.service.impl;

import com.easypark.reports.entity.DevTimeTotal;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.service.TotalService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import java.time.temporal.WeekFields;
import java.util.List;

import static com.easypark.reports.util.WorkBookHelper.START_ROW_NUM;

@Service
public class TotalServiceImpl implements TotalService {

    @Override
    public DevTimeTotal getDevTimes(List<TimeReport> timeReports, String user) {
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

    @Override
    public double countTotal(Sheet sheet, int cellNum) {
        double total = 0;
        int lastRowNum = sheet.getPhysicalNumberOfRows();
        for (int i = START_ROW_NUM + 1; i < lastRowNum + START_ROW_NUM; i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(cellNum);
            total += Double.parseDouble(cell.getStringCellValue());
        }
        return total;
    }
}
