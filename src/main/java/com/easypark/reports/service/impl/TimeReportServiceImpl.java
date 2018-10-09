package com.easypark.reports.service.impl;

import com.easypark.reports.client.JiraTimeReportClient;
import com.easypark.reports.entity.Month;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.entity.jira.response.Issues;
import com.easypark.reports.entity.jira.worklog.WorkReport;
import com.easypark.reports.service.TimeReportService;
import com.easypark.reports.util.NameCreator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isEmpty;

@Service
@AllArgsConstructor
public class TimeReportServiceImpl implements TimeReportService {
    private final JiraTimeReportClient reportClient;

    @Override
    public Map<String, List<TimeReport>> getGroupedTimeReports(Month monthRange) {
        HttpEntity headers = createHeadersForClient();
        List<Issues> issues = reportClient.getIssues(monthRange, headers);
        return createTimeReports(issues, monthRange, headers).stream()
                .collect(Collectors.groupingBy(TimeReport::getAuthorName));
    }

    @Override
    public List<String> getAllTimeReportKeys(Map<String, List<TimeReport>> groupedTimeReports) {
        return groupedTimeReports.entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    private List<TimeReport> createTimeReports(List<Issues> issues, Month monthRange, HttpEntity headers) {
        return issues.stream()
                .map(issue ->
                        reportClient.getWorkLogsByIssuesId(issue, headers)
                                .getWorkLogs().stream()
                                .filter(comparingDate(monthRange))
                                .map(workReport -> new TimeReport(NameCreator.createNameFromKey(workReport.getAuthor().getKey()),
                                        issue.getKey(), workReport.getStarted(),
                                        workReport.getTimeSpentSeconds() / (double) 3600,
                                        isEmpty(workReport.getComment()) ? "dev conf " + monthRange.getToDate().getYear() : workReport.getComment()))
                                .collect(Collectors.toList())
                )
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(TimeReport::getDate))
                .collect(Collectors.toList());
    }

    private HttpEntity createHeadersForClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + "Ym9oZGFuLmtvcmlubnlpQGVhc3lwYXJrLm5ldDpRd2UxMjMhIQ==");
        return new HttpEntity<>(headers);
    }

    private Predicate<WorkReport> comparingDate(Month monthRange) {
        return workReport -> workReport.getStarted().isAfter(monthRange.getFromDate().minusDays(1))
                && workReport.getStarted().isBefore(monthRange.getToDate().plusDays(1));
    }
}
