package com.easypark.reports.service;

import com.easypark.reports.entity.GroupWorkbook;
import com.easypark.reports.entity.MonthReport;
import com.easypark.reports.entity.User;
import com.easypark.reports.entity.UserGroup;
import com.easypark.reports.service.impl.FileServiceImpl;
import com.easypark.reports.util.DateUtils;
import org.apache.commons.lang3.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;

import java.time.chrono.ChronoLocalDate;
import java.util.List;

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
    private WorkbookService userReportService;
    @Mock
    private ZipService zipService;


    @Test()
    public void testGetReportsResource(){
        Range<ChronoLocalDate> monthRange = DateUtils.createMonthRange(MONTH, YEAR);
        List<User> users = List.of(new User("test", "TEST"));
        MonthReport monthReport = new MonthReport(monthRange, List.of());
        List<GroupWorkbook> workbooks = List.of();
        ByteArrayResource resource = new ByteArrayResource(new byte[]{});
        when(userService.getGroup(UserGroup.GENERAL)).thenReturn(users);
        when(monthReportService.getUsersMonthReport(users, monthRange)).thenReturn(monthReport);
        when(userReportService.createUsersWorkbooks(monthReport)).thenReturn(workbooks);
        when(zipService.writeToZip(workbooks)).thenReturn(resource);
        assertThat(fileService.getReportsResource(MONTH, YEAR), is(resource));
    }
}
