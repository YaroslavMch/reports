package com.easypark.reports.client.impl;

import com.easypark.reports.client.JiraClient;
import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.Issue;
import com.easypark.reports.entity.jira.response.IssuesResponse;
import com.easypark.reports.entity.jira.response.WorkLogsResponse;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class JiraClientImpl implements JiraClient {
    private static final String JQL = "worklogDate >= %s and worklogDate <= %s and worklogAuthor in (%s)";

    private final RestTemplate restTemplate;
    private final String jiraPass;

    public JiraClientImpl(
            RestTemplate restTemplate,
            @Value("#{environment.JIRA_PASSWORD}") String jiraPass
    ) {
        this.restTemplate = restTemplate;
        this.jiraPass = jiraPass;
    }

    @Override
    public List<WorkLog> getWorkLogs(String issueKey) {
        URI uri = buildSearchWorkLogsUri(issueKey);
        WorkLogsResponse workLogsResponse = restTemplate.exchange(uri, HttpMethod.GET, getAuthHttpEntity(), WorkLogsResponse.class).getBody();
        if (Objects.isNull(workLogsResponse)) {
            log.error("Cant fetch worklogs with issue key: {}", issueKey);
            throw new RuntimeException("Cant fetch worklogs!");
        }
        return workLogsResponse.getWorkLogs();
    }

    @Override
    public List<Issue> getUserMonthIssues(String user, CustomMonth month) {
        List<Issue> issues = new ArrayList<>();
        int startAt = 0;
        int maxResults = 100;
        int total = 0;
        int processedIssues;
        do {
            URI uri = buildSearchIssuesUri(month, user, startAt, maxResults);
            IssuesResponse jiraIssueResponse = restTemplate.exchange(uri, HttpMethod.GET, getAuthHttpEntity(), IssuesResponse.class).getBody();
            if (Objects.nonNull(jiraIssueResponse)) {
                total = jiraIssueResponse.getTotal();
                issues.addAll(jiraIssueResponse.getIssues());
                processedIssues = issues.size();
                maxResults = total - processedIssues;
                startAt = processedIssues;
            }
        } while (issues.size() < total);
        return issues;
    }

    private URI buildSearchWorkLogsUri(String issueKey) {
        return UriComponentsBuilder
                .fromUriString("https://easypark.jira.com/rest/api/2/")
                .path("issue/" + issueKey + "/worklog")
                .build()
                .encode()
                .toUri();
    }

    private URI buildSearchIssuesUri(CustomMonth month, String user, int startAt, int maxResults) {
        return UriComponentsBuilder
                .fromUriString("https://easypark.jira.com/rest/api/2/")
                .path("search")
                .queryParam("startAt", startAt)
                .queryParam("maxResults", maxResults)
                .queryParam("fields", "issueKey, summary")
                .queryParam("jql", String.format(JQL, month.getFromDate(), month.getToDate(), user))
                .build()
                .encode()
                .toUri();
    }

    private HttpEntity<String> getAuthHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + jiraPass);
        return new HttpEntity<>(headers);
    }
}
