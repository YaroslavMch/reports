package com.easypark.reports.service.impl;

import com.easypark.reports.client.JiraClient;
import com.easypark.reports.entity.*;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import com.easypark.reports.properties.JiraProperties;
import com.easypark.reports.service.MonthReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoField;
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

    public MonthReport getUsersMonthReport(List<String> users, CustomMonth month) {
        Map<String, Double> usersIllnessDays = new HashMap<>();
        Map<String, Double> usersVacationDays = new HashMap<>();
        List<UserMonthReport> reports = users
                .parallelStream()
                .map(user -> getUserMonthReport(user, month))
                .collect(Collectors.toUnmodifiableList());
        findIllnessVacationReports(reports, usersIllnessDays, usersVacationDays);
        return new MonthReport(month.getNumberOfWeeks(), reports, usersIllnessDays, usersVacationDays);
    }

    @Override
    public UserMonthReport validateUserMonthReport(UserMonthReport userMonthReport, String regex) {
        List<TimeReport> filteredReports = userMonthReport.getReports()
                .stream()
                .filter(report -> report.getIssueKey().matches(regex))
                .collect(Collectors.toUnmodifiableList());
        Map<Integer, Double> weeksWorkHours = new HashMap<>();
        if (!filteredReports.isEmpty()) {
            for (TimeReport report : filteredReports) {
                int week = report.getStarted().get(ChronoField.ALIGNED_WEEK_OF_MONTH);
                weeksWorkHours.merge(week, report.getHoursSpent(), Double::sum);
            }
        }
        return new UserMonthReport(userMonthReport.getDeveloperName(), 0.0,
                userMonthReport.getIllnessDays(), userMonthReport.getVacationDays(), weeksWorkHours, filteredReports);
    }

    private UserMonthReport getUserMonthReport(String user, CustomMonth month) {
        double workDayHours = 8.0;
        List<TimeReport> reports = new ArrayList<>();
        double workHours = 0;
        double vacationHours = 0;
        double illnessHours = 0;
        Map<Integer, Double> weeksWorkHours = new HashMap<>();
        for (Issue issue : jiraClient.getUserMonthIssues(user, month)) {
            for (WorkLog workLog : jiraClient.getWorkLogs(issue.getKey())) {
                if (isValidDate(workLog, month)) {
                    if (isValidAuthor(user, workLog)) {
                        double timeSpentHours = workLog.getTimeSpentSeconds() / ONE_HOUR_IN_SECONDS;
                        if (issue.getKey().equals(jiraProperties.getIllKey())) {
                            illnessHours += timeSpentHours;
                        }
                        else if (issue.getKey().equals(jiraProperties.getVacationKey())) {
                            vacationHours += timeSpentHours;
                        } else {
                            workHours += timeSpentHours;
                            int week = workLog.getStarted().get(ChronoField.ALIGNED_WEEK_OF_MONTH);
                            weeksWorkHours.merge(week, timeSpentHours, Double::sum);
                            reports.add(buildReport(issue, workLog));
                        }
                    }
                }
            }
        }
        return new UserMonthReport(user, workHours, illnessHours / workDayHours,
                vacationHours / workDayHours, weeksWorkHours, sortReportsByDate(reports));
    }

    private void findIllnessVacationReports(List<UserMonthReport> reports,
                                            Map<String, Double> usersIllnessDays, Map<String, Double> usersVacationDays) {
        for (UserMonthReport report: reports) {
            if (report.getIllnessDays() > 0) {
                usersIllnessDays.merge(report.getDeveloperName(), report.getIllnessDays(), Double::sum);
            }
            if (report.getVacationDays() > 0) {
                usersVacationDays.merge(report.getDeveloperName(), report.getVacationDays(), Double::sum);
            }
        }
    }

    private List<TimeReport> sortReportsByDate(List<TimeReport> reports) {
        return reports
                .parallelStream()
                .sorted(Comparator.comparing(TimeReport::getStarted))
                .collect(Collectors.toUnmodifiableList());
    }

    private TimeReport buildReport(Issue issue, WorkLog workLog) {
        return new TimeReport(
                getUserKey(workLog.getAuthorName()),
                issue.getKey(),
                workLog.getStarted(),
                workLog.getTimeSpentSeconds() / ONE_HOUR_IN_SECONDS,
                isBlank(workLog.getComment()) ? issue.getSummary() : workLog.getComment(),
                issue.getSummary());
    }

    private String getUserKey(String displayName) {
        if ("Anastasiia Bilokon".equals(displayName)) {
            return "astasiia.bilokon";
        }
        if ("Vitalii Horodetskyi".equals(displayName)) {
            return "5fbe3451cbead50069310a6c";
        }
        if ("Roman Shvets".equals(displayName)) {
            return "60193493a41ea5006a945ad6";
        }
        return displayName.replaceAll(" ", ".").toLowerCase();
    }

    private boolean isValidDate(WorkLog workLog, CustomMonth month) {
        return workLog.getStarted().isAfter(month.getFromDate().minusDays(1))
                && workLog.getStarted().isBefore(month.getToDate().plusDays(1));
    }

    private boolean isValidAuthor(String user, WorkLog workLog) {
        return user.equals(getUserKey(workLog.getAuthorName()));
    }
}
