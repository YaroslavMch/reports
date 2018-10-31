package com.easypark.reports.entity;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DevTimeTotal {
    private final Map<Integer, Double> weeks = Maps.newHashMap();
    private String developerName;
    private double total;

    public void putWeek(int numberWeek, double hour) {
        this.weeks.put(numberWeek, hour);
    }
}
