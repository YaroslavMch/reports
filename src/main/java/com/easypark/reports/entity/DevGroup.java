package com.easypark.reports.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DevGroup {
    SERVER_DEV("ServerDevelopment", ".*"),
    WEB_DEV("ParkingDashboard", ".*"),
    EASY_PARK_APP("EPAppDevelopment", "NEWAPPS.*"),
    MAPPER_APP("MapperAppDevelopment", "^(MAN.*|PGH.*)$");

    private final String name;
    private final String regex;

    public String getRegex() {
        return regex;
    }

    public String getName() {
        return name;
    }
}
