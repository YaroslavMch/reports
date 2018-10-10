package com.easypark.reports.entity.jira.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {
    private String id;
    private String key;
    @JsonProperty("fields")
    private Field field;
}
