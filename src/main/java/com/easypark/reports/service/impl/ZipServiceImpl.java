package com.easypark.reports.service.impl;

import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.service.FileService;
import com.easypark.reports.service.ZipService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@AllArgsConstructor
public class ZipServiceImpl implements ZipService {
    private final FileService fileService;

    @Override
    @SneakyThrows
    public void writeToZip(OutputStream outputStream, List<GroupWorkBook> workbooks) {
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        for (GroupWorkBook workbook : workbooks) {
            try {
                Workbook doc = workbook.getWorkbook();
                ByteArrayOutputStream wbOutputStream = new ByteArrayOutputStream();
                doc.write(wbOutputStream);
                zipOutputStream.putNextEntry(new ZipEntry(workbook.getGroupName() + ".xls"));
                wbOutputStream.writeTo(zipOutputStream);
                zipOutputStream.closeEntry();
            } catch (Exception e) {
                log.error("File doesn't create");
            }
        }
        zipOutputStream.close();
    }
}
