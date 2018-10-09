package com.easypark.reports.client;

import com.easypark.reports.entity.Month;
import com.easypark.reports.entity.jira.response.Issues;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import org.springframework.http.HttpEntity;

import java.util.List;

public interface JiraTimeReportClient {
    List<Issues> getIssues(Month monthRange, HttpEntity headers);

    WorkLog getWorkLogsByIssuesId(Issues issues, HttpEntity headers);
}
