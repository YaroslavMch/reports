package com.easypark.reports.entity.jira.worklog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdfNode {
    String type;
    String text;
    List<AdfNode> content;

    public AdfNode(
            @JsonProperty("type") String type,
            @JsonProperty("text") String text,
            @JsonProperty("content") List<AdfNode> content
    ) {
        this.type = type;
        this.text = text;
        this.content = content;
    }

    public String extractText() {
        if ("text".equals(type) && text != null) {
            return text;
        }
        if (content != null && !content.isEmpty()) {
            return content.stream()
                    .map(AdfNode::extractText)
                    .collect(Collectors.joining(" "));
        }
        return "";
    }
}