package com.easypark.reports.service;

import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;

public interface FileService {
   Workbook getTimeReportTable(String month, Integer year, HttpServletResponse response);
}
