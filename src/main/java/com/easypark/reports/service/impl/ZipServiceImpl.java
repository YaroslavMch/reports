package com.easypark.reports.service.impl;

import com.easypark.reports.entity.GroupWorkbook;
import com.easypark.reports.service.ZipService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipServiceImpl implements ZipService {

    @Override
    public Resource writeToZip(List<GroupWorkbook> workbooks) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writeToExcel(workbooks, outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Can`t create zip archive!");
        }
    }

    private void writeToExcel(List<GroupWorkbook> workbooks, ByteArrayOutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (GroupWorkbook workbook : workbooks) {
                try (ByteArrayOutputStream wbOutputStream = new ByteArrayOutputStream()) {
                    Workbook doc = workbook.getWorkbook();
                    doc.write(wbOutputStream);
                    zipOutputStream.putNextEntry(new ZipEntry(workbook.getGroupName() + ".xlsx"));
                    wbOutputStream.writeTo(zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }
        }
    }
}
