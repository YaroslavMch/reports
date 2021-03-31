package com.easypark.reports.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserGroup {
    GENERAL("General", ".*"),
    SERVER_DEV("ServerDevelopment", ".*"),
    WEB_DEV("ParkingDashboard", ".*"),
    EASY_PARK_APP("EPAppDevelopment", "^((?!MAN.*|PGH.*).)*$"),
    MAPPER_APP("MapperAppDevelopment", "^(MAN.*)|^(PGH.*)*$"),
    INNOVATION("Innovation", ".*");

    private final String name;
    private final String regex;
}
