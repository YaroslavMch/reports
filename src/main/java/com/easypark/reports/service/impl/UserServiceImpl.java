package com.easypark.reports.service.impl;

import com.easypark.reports.entity.User;
import com.easypark.reports.entity.UserGroup;
import com.easypark.reports.properties.UserGroupProperties;
import com.easypark.reports.service.UserService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.easypark.reports.entity.UserGroup.*;

@Service
public class UserServiceImpl implements UserService {
    private final Map<UserGroup, Callable<Map<String, String>>> userGroups;

    public UserServiceImpl(UserGroupProperties userGroupProperties) {
        this.userGroups = Map.of(
                GENERAL, userGroupProperties::getGeneral,
                OPERATORS_EXPERIENCE, userGroupProperties::getOperatorsExperience,
                DRIVERS_EXPERIENCE, userGroupProperties::getDriversExperience,
                EASY_PARKERS_EXPERIENCE, userGroupProperties::getEasyParkersExperience);
    }

    @SneakyThrows
    @Override
    public List<User> getGroup(UserGroup userGroup) {
        return getUsers(userGroups.get(userGroup).call());
    }

    private List<User> getUsers(Map<String, String> group) {
        return group.keySet()
                .stream()
                .map(key -> new User(group.get(key), key))
                .sorted(Comparator.comparing(User::getDisplayName))
                .collect(Collectors.toUnmodifiableList());
    }
}
