package com.easypark.reports.controller;

import com.easypark.reports.service.FileService;
import com.easypark.reports.service.ZipService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@AllArgsConstructor
public class TimeReportController {
    private final FileService fileService;
    private final ZipService zipService;

    @RequestMapping(value = "time-reports/{month}", produces = "application/zip")
    public ResponseEntity getTimeReports(@PathVariable String month, Integer year, HttpServletResponse response) {
        try {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"time-reports.zip\"");
            zipService.writeToZip(response.getOutputStream(), fileService.getTimeReportTable(month, year, response));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to create xml file", e);
            return ResponseEntity.status(response.getStatus()).build();
        }
    }
}
