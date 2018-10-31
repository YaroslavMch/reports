package com.easypark.reports.workbook;

import com.easypark.reports.entity.DevTimeTotal;
import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.service.TotalService;
import com.easypark.reports.util.NameCreator;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.easypark.reports.util.WorkBookHelper.START_ROW_NUM;
import static com.easypark.reports.util.WorkBookHelper.createBorder;
import static com.easypark.reports.util.WorkBookHelper.createOrdinaryRow;
import static com.easypark.reports.util.WorkBookHelper.createStyleForColorCell;
import static java.util.Collections.singletonList;

@Slf4j
@Getter
public class DevTotal implements Runnable {
    private final Map<String, List<TimeReport>> timeReports;
    private final List<String> users;
    private final TotalService totalService;
    private GroupWorkBook workBook;

    public DevTotal(Map<String, List<TimeReport>> timeReports, List<String> users, TotalService totalService) {
        this.timeReports = timeReports;
        this.users = users;
        this.totalService = totalService;
    }

    @Override
    public void run() {
        getTotalTable(timeReports, users);
    }

    private void getTotalTable(Map<String, List<TimeReport>> timeReports, List<String> users) {
        this.workBook = new GroupWorkBook("total", createWorkBook(timeReports, users));
    }

    private Workbook createWorkBook(Map<String, List<TimeReport>> timeReports, List<String> users) {
        Workbook workBook = new XSSFWorkbook();
        Font font = workBook.createFont();
        font.setBold(true);
        CellStyle colorCell = workBook.createCellStyle();
        colorCell.setFont(font);
        createBorder(colorCell);
        createStyleForColorCell(colorCell);
        CellStyle styleAlignCenter = workBook.createCellStyle();
        createBorder(styleAlignCenter);
        styleAlignCenter.setAlignment(HorizontalAlignment.CENTER);
        Sheet totalSheet = workBook.createSheet("Total");
        int rowNum = START_ROW_NUM;
        createOrdinaryRow(totalSheet, rowNum++,
                Lists.newArrayList("Developer", "Month total"), singletonList(colorCell));
        for (String user : users) {
            try {
                createRows(totalSheet, NameCreator.createNameFromKey(user), rowNum++, timeReports.get(user), Lists.newArrayList(colorCell, styleAlignCenter));
            } catch (Exception e) {
                log.error("Not found time reports for user " + user);
            }
        }
        createOrdinaryRow(totalSheet, rowNum++,
                Lists.newArrayList("Total", String.valueOf(totalService.countTotal(totalSheet, 2))), singletonList(colorCell));
        return workBook;
    }

    private void createRows(Sheet sheet, String name, int rowNum, List<TimeReport> timeReports, List<CellStyle> styles) {
        DevTimeTotal devTimes = totalService.getDevTimes(timeReports, name);
        createOrdinaryRow(sheet, rowNum++, Lists.newArrayList(devTimes.getDeveloperName(), String.valueOf(devTimes.getTotal())),
                Lists.newArrayList(styles.get(1)));
    }
}
