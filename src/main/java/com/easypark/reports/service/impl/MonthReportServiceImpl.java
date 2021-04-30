package com.easypark.reports.service.impl;

import com.easypark.reports.client.JiraClient;
import com.easypark.reports.entity.*;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import com.easypark.reports.properties.JiraProperties;
import com.easypark.reports.service.*;
import com.easypark.reports.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonthReportServiceImpl implements MonthReportService {
    private static final int ONE_HOUR_IN_SECONDS = 3600;

    private final JiraClient jiraClient;
    private final JiraProperties jiraProperties;
    private final ZipService zipService;
    private final UserService userService;
    private final GroupWorkbookService groupWorkbookService;
    private final TotalWorkbookService totalWorkbookService;

    @Override
    public Resource getReportsResource(String monthName, Integer year) {
        Range<ChronoLocalDate> monthRange = DateUtils.createMonthRange(monthName, year);
        MonthReport monthReport = getUsersMonthReport(userService.getGroup(UserGroup.GENERAL), monthRange);
        List<GroupWorkbook> usersWorkbooks = groupWorkbookService.createUsersWorkbooks(monthReport);
        usersWorkbooks.add(totalWorkbookService.createTotalWorkbook(monthReport));
        return zipService.writeToZip(usersWorkbooks);
    }

    @Override
    public MonthReport getUsersMonthReport(List<User> users, Range<ChronoLocalDate> monthRange) {
        List<UserMonthReport> reports = users
                .parallelStream()
                .map(user -> getUserMonthReport(user, monthRange))
                .collect(Collectors.toUnmodifiableList());
        return new MonthReport(monthRange, reports);
    }

    private UserMonthReport getUserMonthReport(User user, Range<ChronoLocalDate> monthRange) {
        final double workDayHours = 8.0;
        List<Report> reports = new ArrayList<>();
        double vacationHours = 0;
        double illnessHours = 0;
        Map<Integer, Double> weeksWorkHours = new HashMap<>();
        for (Issue issue : jiraClient.getUserMonthIssues(user.getAccountId(), monthRange)) {
            for (WorkLog workLog : jiraClient.getWorkLogs(issue.getKey())) {
                if (monthRange.contains(workLog.getStarted())) {
                    if (user.getAccountId().equals(workLog.getAuthor().getAccountId())) {
                        double timeSpentHours = workLog.getTimeSpentSeconds() / ONE_HOUR_IN_SECONDS;
                        if (issue.getKey().equals(jiraProperties.getIllnessKey())) {
                            illnessHours += timeSpentHours;
                        } else if (issue.getKey().equals(jiraProperties.getVacationKey())) {
                            vacationHours += timeSpentHours;
                        } else {
                            int week = workLog.getStarted().get(WeekFields.ISO.weekOfMonth());
                            weeksWorkHours.merge(week, timeSpentHours, Double::sum);
                            reports.add(buildReport(issue, workLog));
                        }
                    }
                }
            }
        }
        return new UserMonthReport(user, illnessHours / workDayHours,
                vacationHours / workDayHours, weeksWorkHours, sortReportsByDate(reports));
    }

    private List<Report> sortReportsByDate(List<Report> reports) {
        return reports
                .parallelStream()
                .sorted(Comparator.comparing(Report::getStarted))
                .collect(Collectors.toUnmodifiableList());
    }

    private Report buildReport(Issue issue, WorkLog workLog) {
        return new Report(
                issue.getKey(),
                workLog.getStarted(),
                workLog.getTimeSpentSeconds() / ONE_HOUR_IN_SECONDS,
                isBlank(workLog.getComment()) ? issue.getSummary() : workLog.getComment(),
                issue.getSummary());
    }
}
