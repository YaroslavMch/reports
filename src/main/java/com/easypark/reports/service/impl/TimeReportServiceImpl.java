package com.easypark.reports.service.impl;

import com.easypark.reports.client.JiraTimeReportClient;
import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.TimeReport;
import com.easypark.reports.entity.jira.response.Issue;
import com.easypark.reports.entity.jira.worklog.WorkReport;
import com.easypark.reports.service.TimeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.easypark.reports.util.GroupHelper.getAllGroups;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@Service
@RequiredArgsConstructor
public class TimeReportServiceImpl implements TimeReportService {
    private final JiraTimeReportClient reportClient;
    private final TimeReportProperties timeReportProperties;
    @Value("#{environment.JIRA_PASSWORD}")
    private String password;

    @Override
    public Map<String, List<TimeReport>> getGroupedTimeReports(CustomMonth month) {
        HttpEntity headers = createHeadersForClient();
        List<Issue> issues = reportClient.getIssues(month, headers);
        return createTimeReports(issues, month, headers).stream()
                .collect(Collectors.groupingBy(TimeReport::getKeyAuthor));
    }

    private List<TimeReport> createTimeReports(List<Issue> issues, CustomMonth month, HttpEntity headers) {
        return issues.parallelStream()
                .map(issue ->
                        reportClient.getWorkLogsByIssuesId(issue, headers, Arrays.asList(getAllGroups(timeReportProperties)))
                                .getWorkLogs().stream()
                                .filter(comparingDate(month))
                                .map(workReport -> new TimeReport(
                                        createName(workReport.getAuthor().getDisplayName()),
                                        issue.getKey(), workReport.getStarted(),
                                        workReport.getTimeSpentSeconds() / 3600.,
                                        isEmpty(workReport.getComment()) ? issue.getField().getSummary() : workReport.getComment(),
                                        issue.getField().getSummary()))
                                .collect(Collectors.toList())
                )
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(TimeReport::getDate))
                .collect(Collectors.toList());
    }

    private String createName(String displayName) {
        if ("Anastasiia Bilokon".equals(displayName)) {
            return "astasiia.bilokon";
        }
        if ("Mikhailo Lisoviy".equals(displayName)) {
            return "milkhailo.lisoviy";
        }
        return displayName.replaceAll(" ", ".").toLowerCase();
    }

    private HttpEntity createHeadersForClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + password);
        return new HttpEntity<>(headers);
    }

    private Predicate<WorkReport> comparingDate(CustomMonth monthRange) {
        return workReport -> workReport.getStarted().isAfter(monthRange.getFromDate().minusDays(1))
                && workReport.getStarted().isBefore(monthRange.getToDate().plusDays(1));
    }
}
