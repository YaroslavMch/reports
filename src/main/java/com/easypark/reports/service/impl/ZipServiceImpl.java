package com.easypark.reports.service.impl;

import com.easypark.reports.entity.GroupWorkBook;
import com.easypark.reports.service.ZipService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ZipServiceImpl implements ZipService {

    @Override
    public ByteArrayOutputStream writeToZip(List<GroupWorkBook> workbooks) throws IOException {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(bos)
        ) {
            for (GroupWorkBook workbook : workbooks) {
                try (ByteArrayOutputStream wbOutputStream = new ByteArrayOutputStream()) {
                    Workbook doc = workbook.getWorkbook();
                    doc.write(wbOutputStream);
                    zos.putNextEntry(new ZipEntry(workbook.getGroupName() + ".xls"));
                    wbOutputStream.writeTo(zos);
                    zos.closeEntry();
                }
            }
            return bos;
        } catch (IOException e) {
            String message = "File does not created!";
            log.error(message);
            throw new IOException(message);
        }
    }
}
