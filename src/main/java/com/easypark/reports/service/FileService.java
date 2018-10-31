package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkBook;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileService {
   List<GroupWorkBook> getAllWorkBooks(String month, Integer year, HttpServletResponse response);
}
