package com.easypark.reports.service.impl;

import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.service.FileService;
import com.easypark.reports.service.ZipService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class ZipServiceImpl implements ZipService {
    private final FileService fileService;

    @Override
    @SneakyThrows
    public void writeToZip(OutputStream outputStream, List<GroupWorkBook> workbooks) {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (GroupWorkBook workbook : workbooks) {
            ByteArrayOutputStream wbOutputStream = new ByteArrayOutputStream();
            workbook.getWorkbook().write(wbOutputStream);
            zipOutputStream.putNextEntry(new ZipEntry(workbook.getGroupName() + ".xls"));
            wbOutputStream.writeTo(zipOutputStream);
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();
    }
}
