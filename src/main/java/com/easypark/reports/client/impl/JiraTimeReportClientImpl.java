package com.easypark.reports.client.impl;

import com.easypark.reports.client.JiraTimeReportClient;
import com.easypark.reports.configuration.JiraProperties;
import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.Month;
import com.easypark.reports.entity.jira.response.Issues;
import com.easypark.reports.entity.jira.response.JiraResponse;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JiraTimeReportClientImpl implements JiraTimeReportClient {
    private static final String JQL = "worklogDate >= %s and worklogDate <= %s and worklogAuthor in (%s)";
    private static final int MAX_RESULT = 100;
    private final RestTemplate restTemplate;
    private final JiraProperties jiraProperties;
    private final TimeReportProperties reportProperties;

    @Override
    public List<Issues> getIssues(Month monthRange, HttpEntity headers) {
        List<JiraResponse> jiraResponses = getJiraResponse(monthRange, headers);
        return jiraResponses.stream()
                .map(JiraResponse::getIssues)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public WorkLog getWorkLogsByIssuesId(Issues issues, HttpEntity headers) {
        List<String> users = Arrays.asList(reportProperties.getUsers());
        WorkLog allWorks = restTemplate
                .exchange(createUriForWorkLogApi(issues.getId()), HttpMethod.GET, headers, WorkLog.class)
                .getBody();
        return new WorkLog(allWorks.getWorkLogs().stream()
                .filter(workReport -> users.contains(workReport.getAuthor().getKey()))
                .collect(Collectors.toList()));
    }

    private List<JiraResponse> getJiraResponse(Month monthRange, HttpEntity headers) {
        int startAt = 0;
        JiraResponse jiraResponse;
        List<JiraResponse> jiraResponses = Lists.newArrayList();
        do {
            URI uri = createUriForSearchApi(startAt, monthRange);
            jiraResponse = restTemplate.exchange(uri, HttpMethod.GET, headers, JiraResponse.class).getBody();
            startAt = jiraResponse.getStartAt() + MAX_RESULT;
            jiraResponses.add(jiraResponse);
        } while (jiraResponse.getTotal() - startAt >= 1);
        return jiraResponses;
    }

    private URI createUriForSearchApi(int startAt, Month monthRange) {
        return UriComponentsBuilder
                .fromUriString(jiraProperties.getDomain())
                .path(jiraProperties.getPath() + "search")
                .queryParam("startAt", startAt)
                .queryParam("maxResults", MAX_RESULT)
                .queryParam("jql", String.format(JQL, monthRange.getFromDate(), monthRange.getToDate(), Stream.of(reportProperties.getUsers()).collect(Collectors.joining(","))))
                .build()
                .encode()
                .toUri();
    }

    private URI createUriForWorkLogApi(String id) {
        return UriComponentsBuilder
                .fromUriString(jiraProperties.getDomain())
                .path(jiraProperties.getPath() + "issue/" + id + "/worklog")
                .build()
                .encode()
                .toUri();
    }
}
