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

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Pavel on 04.05.2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
public class JobServiceTest {
    private static final String PREFIX = "tempFile";
    private static final String SUFFIX = "csv";

    private BufferedWriter bufferedWriter;
    private FileWriter fileWriter;
    private CSVPrinter csvPrinter;
    private InputStreamResource inputStreamResource;
    private MockMultipartFile badFile;
    private MockMultipartFile goodFile;
    private File file;

    @Autowired
    @Qualifier("JobServiceImpl")
    private JobServiceImpl jobService;

    private EntityServiceImpl entityService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;





    @Before
    public void setUp() throws IOException {
        bufferedWriter = mock(BufferedWriter.class);
        fileWriter = mock(FileWriter.class);
        csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT);
        inputStreamResource = mock(InputStreamResource.class);
        file = File.createTempFile(PREFIX, SUFFIX);
        entityService = mock(EntityServiceImpl.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        goodFile = new MockMultipartFile
                ("file", "excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//excel.xlsx")));


        badFile = new MockMultipartFile("file", "pom",
                "application/xml",
                FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//pom.xml")));

    }


    @Test(expected = EmptyDbException.class)
    public void throwEmptyDbExceptionWhenDbIsEmpty() throws IOException, EmptyDbException {
        ArrayList<Job> list = new ArrayList<>();
        assertEquals(0, list.size());
        when(entityService.findAll()).thenReturn(list);
        assertSame(EmptyDbException.class, jobService.read());
        assertNotSame(jobService.read(), InputStreamResource.class);

    }

    @Test(expected = EmptyDbException.class)
    public void serviceShouldReturnInputStreamResource() throws IOException, EmptyDbException {
        ArrayList<Job> list = new ArrayList<>();
        list.add(new Job());
        when(entityService.findAll()).thenReturn(list);
        assertEquals(1, list.size());
        assertNotNull(file);
        assertNotNull(inputStreamResource);
        assertNotNull(fileWriter);
        assertNotNull(csvPrinter);
        assertTrue(jobService.read() instanceof InputStreamResource);
    }


    @Test
    public void serviceShouldReturnCountOfSkippedAndWritingRows() throws Exception{
        jobService.write(goodFile);
    }
}
