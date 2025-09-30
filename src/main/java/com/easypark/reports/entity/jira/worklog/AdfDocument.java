package com.easypark.reports.entity.jira.worklog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdfDocument {
    String type;
    int version;
    List<AdfNode> content;

    public AdfDocument(
            @JsonProperty("type") String type,
            @JsonProperty("version") int version,
            @JsonProperty("content") List<AdfNode> content
    ) {
        this.type = type;
        this.version = version;
        this.content = content;
    }

    public String extractText() {
        if (content == null || content.isEmpty()) {
            return "";
        }
        return content.stream()
                .map(AdfNode::extractText)
                .collect(Collectors.joining(" "));
    }
}