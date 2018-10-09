package com.easypark.reports.util;

import com.easypark.reports.entity.Month;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Slf4j
public class MonthParser {
    private static final String ERROR_MESSAGE = "Month with name %s does not exist!";

    public static Month getMonthRange(String monthName, int year, HttpServletResponse httpResponse) {
        Month month;
        switch (monthName) {
            case "january": {
                LocalDate initial = LocalDate.of(year, 1, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "february": {
                LocalDate initial = LocalDate.of(year, 2, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "march": {
                LocalDate initial = LocalDate.of(year, 3, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "april": {
                LocalDate initial = LocalDate.of(year, 4, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "may": {
                LocalDate initial = LocalDate.of(year, 5, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "june": {
                LocalDate initial = LocalDate.of(year, 6, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "july": {
                LocalDate initial = LocalDate.of(year, 7, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "august": {
                LocalDate initial = LocalDate.of(year, 8, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "september": {
                LocalDate initial = LocalDate.of(year, 9, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "october": {
                LocalDate initial = LocalDate.of(year, 10, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "november": {
                LocalDate initial = LocalDate.of(year, 11, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            case "december": {
                LocalDate initial = LocalDate.of(year, 12, 1);
                month = new Month(initial.withDayOfMonth(1), initial.withDayOfMonth(initial.lengthOfMonth()));
                break;
            }
            default: {
                log.error(String.format(ERROR_MESSAGE, monthName));
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new RuntimeException(String.format(ERROR_MESSAGE, monthName));
            }
        }
        return month;
    }
}
