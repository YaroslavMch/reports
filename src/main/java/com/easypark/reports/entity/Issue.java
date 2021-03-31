package com.easypark.reports.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.Map;

@Value
public class Issue {
    String id;
    String key;
    String summary;

    @JsonCreator
    public Issue(
            @JsonProperty("id") String id,
            @JsonProperty("key") String key,
            @JsonProperty("fields") Map<String, String> fields) {
        this.id = id;
        this.key = key;
        this.summary = fields.get("summary");
    }
}
