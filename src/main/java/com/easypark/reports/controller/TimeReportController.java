package com.easypark.reports.controller;

import com.easypark.reports.service.FileService;
import com.easypark.reports.service.ZipService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("time-reports")
public class TimeReportController {
    private final FileService fileService;
    private final ZipService zipService;

    @GetMapping(produces = "application/zip")
    public ResponseEntity getTimeReports(String month, Integer year, HttpServletResponse response) {
        try {
            zipService.writeToZip(response.getOutputStream(), fileService.getAllWorkBooks(month, year, response));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + month + ".zip\"");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to create xml file", e.getMessage());
            return ResponseEntity.status(response.getStatus()).body(e.getMessage());
        }
    }
}
