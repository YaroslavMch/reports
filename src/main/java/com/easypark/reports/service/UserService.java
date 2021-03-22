package com.easypark.reports.service;

import com.easypark.reports.entity.DevGroup;

import java.util.List;

public interface UserService {

    List<String> getGroup(DevGroup devGroup);

    List<String> getAllGroups();
}
