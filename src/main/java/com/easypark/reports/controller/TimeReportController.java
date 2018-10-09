package com.easypark.reports.controller;

import com.easypark.reports.service.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@AllArgsConstructor
public class TimeReportController {
    private final FileService fileService;

    @GetMapping("time-reports/{month}")
    public ResponseEntity getTimeReports(@PathVariable String month, Integer year, HttpServletResponse response) {
        try {
            Workbook workbook = fileService.getTimeReportTable(month, year, response);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"time-reports.xls\"");
            workbook.write(response.getOutputStream());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to create xml file", e);
            return ResponseEntity.status(response.getStatus()).build();
        }
    }
}
