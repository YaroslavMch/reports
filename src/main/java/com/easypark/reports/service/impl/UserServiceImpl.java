package com.easypark.reports.service.impl;

import com.easypark.reports.properties.TimeReportProperties;
import com.easypark.reports.entity.DevGroup;
import com.easypark.reports.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final TimeReportProperties timeReportProperties;

    @Override
    public List<String> getGroup(DevGroup devGroup) {
        return switch (devGroup) {
            case SERVER_DEV -> sortGroup(timeReportProperties.getServer());
            case WEB_DEV -> sortGroup(timeReportProperties.getWeb());
            case EASY_PARK_APP, MAPPER_APP -> sortGroup(timeReportProperties.getApp());
            case GENERAL -> sortGroup(timeReportProperties.getGeneral());
            case INNOVATION -> sortGroup(timeReportProperties.getInnovation());
        };
    }

    @Override
    public List<String> getAllGroups() {
        return sortGroup(timeReportProperties.getGeneral());
    }

    private List<String> sortGroup(List<String> group) {
        return group
                .stream()
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }
}
