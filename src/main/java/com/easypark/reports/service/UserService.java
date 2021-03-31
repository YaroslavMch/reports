package com.easypark.reports.service;

import com.easypark.reports.entity.UserGroup;
import com.easypark.reports.entity.User;

import java.util.List;

public interface UserService {

    List<User> getGroup(UserGroup userGroup);
}
