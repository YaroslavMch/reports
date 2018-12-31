package com.easypark.reports.service;

import com.easypark.reports.configuration.TimeReportProperties;
import com.easypark.reports.service.impl.FileServiceImpl;
import com.easypark.reports.util.MonthParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceImplTest {
    private static final String MONTH = "december";
    private static final int YEAR = 2018;

    @InjectMocks
    private FileServiceImpl fileService;
    @Mock
    private TimeReportService timeReportService;
    @Mock
    private TimeReportProperties timeReportProperties;

    @Test(expected = RuntimeException.class)
    public void testGetAllWorkBooksNoReports(){
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        when(timeReportService.getGroupedTimeReports(MonthParser.getMonthRange(MONTH, YEAR, servletResponse))).thenReturn(Collections.emptyMap());
        fileService.getAllWorkBooks(MONTH, YEAR, servletResponse);
        assertThat(servletResponse.getStatus(), is(500));
    }
}
