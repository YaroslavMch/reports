package com.easypark.reports.service;

import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.properties.TimeReportProperties;
import com.easypark.reports.service.impl.FileServiceImpl;
import com.easypark.reports.service.impl.WorkbookServiceImpl;
import com.easypark.reports.util.MonthParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Map;

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
    private MonthReportService monthReportService;
    @Mock
    private UserService userService;
    @Mock
    private WorkbookServiceImpl userReportServiceImpl;


    @Test()
    public void testGetAllWorkBooksReports(){
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        List<String> users = userService.getAllGroups();
        MonthReport expectedReport = new MonthReport(4, List.of(), Map.of(), Map.of());
        when(monthReportService.getUsersMonthReport(users, MonthParser.getMonthRange(MONTH, YEAR))).thenReturn(expectedReport);
        fileService.getAllWorkBooks(MONTH, YEAR);
        assertThat(servletResponse.getStatus(), is(200));
    }
}
