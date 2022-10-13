package com.easypark.reports.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Range;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Arrays;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static final TemporalField WEEK_OF_MONTH = WeekFields.of(DayOfWeek.SUNDAY, 1).weekOfMonth();

    public static int getNumberOfWeeks(Range<ChronoLocalDate> monthRange) {
        return monthRange.getMaximum().get(WEEK_OF_MONTH);
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
