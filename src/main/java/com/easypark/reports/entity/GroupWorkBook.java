package com.easypark.reports.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Workbook;

@Getter
@AllArgsConstructor
public class GroupWorkBook {
    private final String groupName;
    private final Workbook workbook;
}
