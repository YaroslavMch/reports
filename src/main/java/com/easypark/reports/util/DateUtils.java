package com.easypark.reports.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Range;

import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static int getNumberOfWeeks(Range<ChronoLocalDate> monthRange) {
        return monthRange.getMaximum().lengthOfMonth() / (double) 7 > 4 ? 5 : 4;
    }

    public static Range<ChronoLocalDate> createMonthRange(String monthName, int year) {
        LocalDate firstDayOfMonth = LocalDate.parse(1 + "/" + parseMonth(monthName).getValue() + "/" + year, DATE_TIME_FORMATTER);
        return Range.between(firstDayOfMonth, firstDayOfMonth.with(lastDayOfMonth()));
    }

    private static Month parseMonth(String monthName) {
        return Arrays.stream(Month.values())
                .filter(month -> month.name().equalsIgnoreCase(monthName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can`t parse month: " + monthName));
    }
}
