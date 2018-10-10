package com.easypark.reports.util;

import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.entity.DevGroup;
import com.google.common.collect.ObjectArrays;

import java.util.Arrays;
import java.util.List;

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
        }
        return Arrays.asList(group);
    }

    public static String[] getAllGroups(TimeReportProperties timeReportProperties) {
        return ObjectArrays.concat(ObjectArrays.concat(timeReportProperties.getServer(),
                timeReportProperties.getApp(), String.class), timeReportProperties.getWeb(), String.class);
    }
}
