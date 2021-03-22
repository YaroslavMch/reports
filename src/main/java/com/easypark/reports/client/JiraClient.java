package com.easypark.reports.client;

import com.easypark.reports.entity.CustomMonth;
import com.easypark.reports.entity.Issue;
import com.easypark.reports.entity.jira.worklog.WorkLog;

import java.util.List;

public interface JiraClient {

    List<WorkLog> getWorkLogs(String issueKey);

    List<Issue> getUserMonthIssues(String user, CustomMonth month);
}
