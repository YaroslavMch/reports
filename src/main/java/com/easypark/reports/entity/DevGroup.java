package com.easypark.reports.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DevGroup {
    SERVER_DEV("server", ".*"),
    WEB_DEV("web", ".*"),
    EASY_PARK_APP("easy-park-app", "NEWAPPS.*"),
    MAPPER_APP("mapper-app", "^(MAN.*|PGH.*)$");

    private final String name;
    private final String regex;

    public String getRegex() {
        return regex;
    }

    public String getName() {
        return name;
    }
}
