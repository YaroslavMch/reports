package com.easypark.reports.util;

import com.easypark.reports.entity.CustomMonth;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.time.Month;

@Slf4j
public class MonthParser {
    private static final String ERROR_MESSAGE = "Month with name %s does not exist!";

    public static CustomMonth getMonthRange(String monthName, int year, HttpServletResponse httpResponse) {
        CustomMonth month;
        String monthUpperCase = monthName.toLowerCase();
        switch (monthUpperCase) {
            case "january": {
                month = new CustomMonth(Month.JANUARY, year);
                break;
            }
            case "february": {
                month = new CustomMonth(Month.FEBRUARY, year);
                break;
            }
            case "march": {
                month = new CustomMonth(Month.MARCH, year);
                break;
            }
            case "april": {
                month = new CustomMonth(Month.APRIL, year);
                break;
            }
            case "may": {
                month = new CustomMonth(Month.MAY, year);
                break;
            }
            case "june": {
                month = new CustomMonth(Month.JUNE, year);
                break;
            }
            case "july": {
                month = new CustomMonth(Month.JULY, year);
                break;
            }
            case "august": {
                month = new CustomMonth(Month.AUGUST, year);
                break;
            }
            case "september": {
                month = new CustomMonth(Month.SEPTEMBER, year);
                break;
            }
            case "october": {
                month = new CustomMonth(Month.OCTOBER, year);
                break;
            }
            case "november": {
                month = new CustomMonth(Month.NOVEMBER, year);
                break;
            }
            case "december": {
                month = new CustomMonth(Month.DECEMBER, year);
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
