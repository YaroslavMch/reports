package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkBook;

import java.io.OutputStream;
import java.util.List;

public interface ZipService {
    void writeToZip(OutputStream outputStream, List<GroupWorkBook> workbooks);
}
