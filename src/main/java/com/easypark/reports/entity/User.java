package com.easypark.reports.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class User {
    String accountId;
    String displayName;

    public User(
            @JsonProperty("accountId") String accountId,
            @JsonProperty("displayName") String displayName
    ) {
        this.accountId = accountId;
        this.displayName = displayName;
    }
}
