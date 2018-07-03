import config.WebAppContext;
import exceptions.EmptyDbException;
import model.Job;
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
import service.EntityService;
import service.impl.JobServiceImpl;

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

    private EntityService<Job> entityService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() throws IOException {
        entityService = mock(EntityService.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        goodFile = new MockMultipartFile(FILE_NAME, ORIGINAL_FILE_NAME, XLSX_TYPE, FILE_NAME.getBytes());


        badFile = new MockMultipartFile(FILE_NAME, ORIGINAL_FILE_NAME, XML_TYPE, FILE_NAME.getBytes());

    }


    @Test
    public void throwEmptyDbExceptionWhenDbIsEmpty() throws IOException, EmptyDbException {
        ArrayList<Job> list = new ArrayList<>();
        when(entityService.findAll()).thenReturn(list);
        assertSame(EmptyDbException.class, jobService.read());

    }

    @Test
    public void serviceShouldReturnInputStreamResource() throws IOException, EmptyDbException {
        ArrayList<Job> list = new ArrayList<>();
        list.add(new Job());
        when(entityService.findAll()).thenReturn(list);
        assertEquals(1, list.size());
        assertTrue(jobService.read() instanceof InputStreamResource);
    }



}
