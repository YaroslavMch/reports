package com.easypark.reports.util;

import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.DevGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING_ARRAY;

public class GroupHelper {

    public static List<String> getGroup(TimeReportProperties timeReportProperties, DevGroup devGroup) {
        String[] group = EMPTY_STRING_ARRAY;
        switch (devGroup) {
            case SERVER_DEV: {
                group = timeReportProperties.getServer();
                break;
            }
            case WEB_DEV: {
                group = timeReportProperties.getWeb();
                break;
            }
            case EASY_PARK_APP: {
                group = timeReportProperties.getApp();
                break;
            }
            case MAPPER_APP: {
                group = timeReportProperties.getApp();
                break;
            }
            case GENERAL: {
                group = timeReportProperties.getGeneral();
                break;
            }
            case INNOVATION: {
                group = timeReportProperties.getInnovation();
                break;
            }
        }
        return Arrays.stream(group)
                .sorted()
                .collect(Collectors.toList());
    }

    public static String[] getAllGroups(TimeReportProperties timeReportProperties) {
        return timeReportProperties.getGeneral();
    }
}
