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


    @Mock
    InputStreamResource inputStreamResource;

    @InjectMocks
    private LoadController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        Mockito.reset(jobService);
        goodFile = new MockMultipartFile
                ("file", "excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//excel.xlsx")));


        badFile = new MockMultipartFile("file", "pom",
                "application/xml",
                FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//pom.xml")));
    }

    @Test
    public void controllerShouldSendCsv() throws Exception {
        doReturn(inputStreamResource).when(jobService).read();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/download")
                .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk());
    }

    @Test
    public void controllerShouldSendBadRequest() throws Exception {
        doThrow(new IOException()).when(jobService).read();
        mockMvc.perform(get("/download"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void controllerShouldSendNotFoundIfDbIsEmpty() throws Exception {
        doThrow(new EmptyDbException()).when(jobService).read();
        mockMvc.perform(get("/download"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void controllerShouldSendStatusOkIfFileWasUploadedInDb() throws Exception {
        Lines lines = new Lines(RECORDED_ROWS, SKIPPED_ROWS);
        doReturn(lines).when(jobService).write(goodFile);
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(goodFile))
                .andExpect(status().isOk());
    }


    @Test
    public void controllerShouldSendBadRequestIfFileHasNotExtensionXlsxOrXls() throws Exception {
        doThrow(new IOException()).when(jobService).write(badFile);
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(badFile))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void controllerShouldSendInternalServerErrorIfSomethingGoesWrong() throws Exception {

        doThrow(new Exception()).when(jobService).write(any(MultipartFile.class));
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(goodFile))
                .andExpect(status().isInternalServerError());
    }
}
