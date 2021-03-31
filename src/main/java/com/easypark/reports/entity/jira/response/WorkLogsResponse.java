package com.easypark.reports.entity.jira.response;

import com.easypark.reports.entity.jira.worklog.WorkLog;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class WorkLogsResponse {
    List<WorkLog> workLogs;

    public WorkLogsResponse(@JsonProperty("worklogs") List<WorkLog> workLogs) {
        this.workLogs = workLogs;
    }
}
