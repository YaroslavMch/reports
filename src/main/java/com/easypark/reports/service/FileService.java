package com.easypark.reports.service;

import org.springframework.core.io.Resource;

public interface FileService {
   Resource getReportsResource(String month, Integer year);
}
