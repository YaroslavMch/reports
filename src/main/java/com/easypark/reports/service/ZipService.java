package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkbook;
import org.springframework.core.io.Resource;

import java.util.List;

public interface ZipService {
    Resource writeToZip(List<GroupWorkbook> workbooks);
}
