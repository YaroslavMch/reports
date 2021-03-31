package com.easypark.reports.client;

import com.easypark.reports.entity.Issue;
import com.easypark.reports.entity.jira.worklog.WorkLog;
import org.apache.commons.lang3.Range;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

public interface JiraClient {

    List<WorkLog> getWorkLogs(String issueKey);

    List<Issue> getUserMonthIssues(String user, Range<ChronoLocalDate> monthRange);
}
