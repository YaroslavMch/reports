package com.easypark.reports.controller;

import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.service.FileService;
import com.easypark.reports.service.ZipService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("time-reports")
public class TimeReportController {
    private final FileService fileService;
    private final ZipService zipService;

    @GetMapping(produces = "application/zip")
    public ResponseEntity<Resource> getTimeReports(@RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonth().toString().toLowerCase()}") String month,
                                                   @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer year) {
        List<GroupWorkBook> workBooks = fileService.getAllWorkBooks(month, year);
        try (ByteArrayOutputStream bos = zipService.writeToZip(workBooks)) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + month + ".zip")
                    .body(new ByteArrayResource(bos.toByteArray()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
