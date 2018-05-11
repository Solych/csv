import config.WebAppContext;
import exceptions.EmptyDbException;
import model.Job;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import service.impl.EntityServiceImpl;
import service.impl.JobServiceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Pavel on 04.05.2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
public class JobServiceTest {

    private static final String FILE_NAME = "file";
    private static final String ORIGINAL_FILE_NAME = "temp";
    private static final String XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XML_TYPE = "application/xml";

    private MockMultipartFile badFile;
    private MockMultipartFile goodFile;

    @Autowired
    @Qualifier("JobServiceImpl")
    private JobServiceImpl jobService;

    private EntityServiceImpl entityService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() throws IOException {
        entityService = mock(EntityServiceImpl.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        goodFile = new MockMultipartFile(FILE_NAME, ORIGINAL_FILE_NAME, XLSX_TYPE, FILE_NAME.getBytes());


        badFile = new MockMultipartFile(FILE_NAME, ORIGINAL_FILE_NAME, XML_TYPE, FILE_NAME.getBytes());

    }


    @Test
    public void throwEmptyDbExceptionWhenDbIsEmpty() throws IOException, EmptyDbException {
        ArrayList<Job> list = new ArrayList<>();
        when(entityService.findAll()).thenReturn(list);
        assertSame(EmptyDbException.class, jobService.read());
        assertNotSame(jobService.read(), InputStreamResource.class);

    }

    @Test
    public void serviceShouldReturnInputStreamResource() throws IOException, EmptyDbException {
        ArrayList<Job> list = new ArrayList<>();
        list.add(new Job());
        when(entityService.findAll()).thenReturn(list);
        assertEquals(1, list.size());
        assertTrue(jobService.read() instanceof InputStreamResource);
    }


    @Test
    public void serviceShouldReturnCountOfSkippedAndWritingRows() throws Exception {
        jobService.write(goodFile);
    }
}
