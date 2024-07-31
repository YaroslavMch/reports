package com.easypark.reports.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserGroup {
    GENERAL("General", ".*"),
//    SERVER_DEV("ServerDevelopment", ".*"),
//    WEB_DEV("ParkingDashboard", ".*"),
//    EASY_PARK_APP("EPAppDevelopment", "^((?!MAN.*|PGH.*).)*$"),
//    MAPPER_APP("MapperAppDevelopment", "^(PDD.*)|^(PGH.*)|^(MAN.*)*$"),
//    INNOVATION("Innovation", ".*"),
    EASY_PARKERS_EXPERIENCE("EasyParker's Experience",".*"),
    DRIVERS_EXPERIENCE("Driver's Experience",".*"),
    OPERATORS_EXPERIENCE("Operator's Experience","^(PDD.*)|^(PGH.*)|^(MAN.*)*$");


    private final String name;
    private final String regex;
}
