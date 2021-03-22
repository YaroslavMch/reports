package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkBook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public interface ZipService {
    ByteArrayOutputStream writeToZip(List<GroupWorkBook> workbooks) throws IOException;
}
