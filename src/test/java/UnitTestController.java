import config.WebAppContext;
import controller.LoadController;
import exceptions.EmptyDbException;
import model.Lines;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import service.impl.JobServiceImpl;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Pavel on 04.05.2018.
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
public class UnitTestController {

    @Mock
    private JobServiceImpl jobService;

    private MockMultipartFile goodFile;
    private MockMultipartFile badFile;

    private static final int RECORDED_ROWS = 1;
    private static final int SKIPPED_ROWS = 1;

    private final static String XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private final static String XML_TYPE = "application/xml";
    private final static String FILE_NAME = "file";
    private final static String ORIGINAL_FILE_NAME = "temp";
    private final static String GET_REQUEST = "/download";
    private final static String POST_REQUEST = "/upload";
    private static final String PATH_TO_XLSX  = "testFiles//timeTable.xlsx";
    private static final String PATH_TO_XML = "testFiles//pom.xml";


    @InjectMocks
    private LoadController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        MockitoAnnotations.initMocks(this);
        goodFile = new MockMultipartFile
                (FILE_NAME, ORIGINAL_FILE_NAME, XLSX_TYPE,
                        FileUtils.readFileToByteArray(new File(PATH_TO_XLSX)));


        badFile = new MockMultipartFile(FILE_NAME, ORIGINAL_FILE_NAME,
                XML_TYPE, FileUtils.readFileToByteArray(new File(PATH_TO_XML)));
    }

    @Test
    public void controllerShouldSendCsv() throws Exception {
        doReturn(mock(InputStreamResource.class)).when(jobService).read();
        mockMvc.perform(RestDocumentationRequestBuilders.get(GET_REQUEST)
                .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk());
    }

    @Test
    public void controllerShouldSendBadRequest() throws Exception {
        doThrow(new IOException()).when(jobService).read();
        mockMvc.perform(get(GET_REQUEST))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void controllerShouldSendNotFoundIfDbIsEmpty() throws Exception {
        doThrow(new EmptyDbException()).when(jobService).read();
        mockMvc.perform(get(GET_REQUEST))
                .andExpect(status().isNotFound());
    }


    @Test
    public void controllerShouldSendStatusOkIfFileWasUploadedInDb() throws Exception {
        Lines lines = new Lines(RECORDED_ROWS, SKIPPED_ROWS);
        doReturn(lines).when(jobService).write(goodFile);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(POST_REQUEST).file(goodFile);
        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }


    @Test
    public void controllerShouldSendBadRequestIfFileHasNotExtensionXlsxOrXls() throws Exception {
        doThrow(new IOException()).when(jobService).write(badFile);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(POST_REQUEST).file(badFile);
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void controllerShouldSendInternalServerErrorIfSomethingGoesWrong() throws Exception {
        doThrow(new Exception()).when(jobService).write(any(MultipartFile.class));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(POST_REQUEST).file(goodFile);
        mockMvc.perform(builder)
                .andExpect(status().isInternalServerError());
    }
}
