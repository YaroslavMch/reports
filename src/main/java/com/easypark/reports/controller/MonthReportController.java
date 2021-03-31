package com.easypark.reports.controller;

import com.easypark.reports.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "time-reports")
public class MonthReportController {
    private final FileService fileService;

    @GetMapping(produces = "application/zip")
    public ResponseEntity<Resource> getReportsZipArchive(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonth().toString().toLowerCase()}") String month,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer year
    ) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + month + ".zip")
                .body(fileService.getReportsResource(month, year));
    }
}
