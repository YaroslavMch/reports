package com.easypark.reports.client.impl;

import com.easypark.reports.client.JiraClient;
import com.easypark.reports.entity.Issue;
import com.easypark.reports.entity.jira.response.IssuesResponse;
import com.easypark.reports.entity.jira.response.WorkLogsResponse;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import com.easypark.reports.properties.JiraProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JiraClientImpl implements JiraClient {
    private static final String JQL = "worklogDate >= %s and worklogDate <= %s and worklogAuthor in (%s)";

    private final RestTemplate restTemplate;
    private final JiraProperties jiraProperties;

    @Override
    public List<WorkLog> getWorkLogs(String issueKey) {
        URI uri = buildSearchWorkLogsUri(issueKey);
        WorkLogsResponse workLogsResponse = restTemplate.exchange(uri, HttpMethod.GET, getAuthHttpEntity(), WorkLogsResponse.class).getBody();
        if (Objects.isNull(workLogsResponse)) {
            throw new RuntimeException("Cant fetch worklogs for issue: " + issueKey + "!");
        }
        return workLogsResponse.getWorkLogs();
    }

    @Override
    public List<Issue> getUserMonthIssues(String user, Range<ChronoLocalDate> monthRange) {
        final int maxResults = 100;
        List<Issue> issues = new ArrayList<>();
        int startAt = 0;
        int total = fetchIssuesResponse(monthRange, user, startAt, maxResults).orElseThrow().getTotal();
        do {
            fetchIssuesResponse(monthRange, user, startAt, maxResults).ifPresent(response -> issues.addAll(response.getIssues()));
        } while (issues.size() < total);
        return issues;
    }

    private URI buildSearchWorkLogsUri(String issueKey) {
        return UriComponentsBuilder
                .fromUriString(jiraProperties.getDomain())
                .path("issue/" + issueKey + "/worklog")
                .build()
                .encode()
                .toUri();
    }

    private URI buildSearchIssuesUri(Range<ChronoLocalDate> monthRange, String user, int startAt, int maxResults) {
        return UriComponentsBuilder
                .fromUriString(jiraProperties.getDomain())
                .path("search")
                .queryParam("startAt", startAt)
                .queryParam("maxResults", maxResults)
                .queryParam("fields", "issueKey, summary")
                .queryParam("jql", String.format(JQL, monthRange.getMinimum(), monthRange.getMaximum(), user))
                .build()
                .encode()
                .toUri();
    }

    private HttpEntity<String> getAuthHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + jiraProperties.getPassword());
        return new HttpEntity<>(headers);
    }

    private Optional<IssuesResponse> fetchIssuesResponse(Range<ChronoLocalDate> monthRange, String user, int startIndex, int maxResults) {
        URI uri = buildSearchIssuesUri(monthRange, user, startIndex, maxResults);
        return Optional.ofNullable(restTemplate.exchange(uri, HttpMethod.GET, getAuthHttpEntity(), IssuesResponse.class).getBody());
    }
}
