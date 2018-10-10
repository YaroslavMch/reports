package com.easypark.reports.client;

import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.jira.response.Issue;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import org.springframework.http.HttpEntity;

import java.util.List;

public interface JiraTimeReportClient {
    List<Issue> getIssues(CustomMonth month, HttpEntity headers);

    WorkLog getWorkLogsByIssuesId(Issue issue, HttpEntity headers, List<String> users);
}
