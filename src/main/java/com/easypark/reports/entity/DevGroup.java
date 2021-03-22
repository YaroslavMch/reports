package com.easypark.reports.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DevGroup {
    GENERAL("General", ".*"),
    SERVER_DEV("ServerDevelopment", ".*"),
    WEB_DEV("ParkingDashboard", ".*"),
    EASY_PARK_APP("EPAppDevelopment", "^((?!MAN.*|PGH.*).)*$"),
    MAPPER_APP("MapperAppDevelopment", "^(MAN.*)|^(PGH.*)*$"),
    INNOVATION("Innovation", ".*");

    private final String name;
    private final String regex;

    public String getRegex() {
        return regex;
    }

    public String getName() {
        return name;
    }
}
