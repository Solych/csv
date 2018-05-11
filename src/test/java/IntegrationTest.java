
import config.WebAppContext;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppContext.class})
@WebAppConfiguration
public class IntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private MockMultipartFile badFile;
    private MockMultipartFile goodFile;

    private final static String XLSX_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private final static String XML_TYPE = "application/xml";
    private final static String FILE_NAME = "file";
    private final static String ORIGINAL_FILE_NAME = "temp";
    private final static String CSV_CONTENT_TYPE = "application/octet-stream";
    private final static String GET_REQUEST = "/download";
    private final static String POST_REQUEST = "/upload";
    private static final String PATH_TO_XLSX  = "testFiles//timeTable.xlsx";
    private static final String PATH_TO_XML = "testFiles//pom.xml";



    @Before
    public void setMockMvc() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        goodFile = new MockMultipartFile
                (FILE_NAME, ORIGINAL_FILE_NAME,
                        XLSX_TYPE,
                        FileUtils.readFileToByteArray(new File(PATH_TO_XLSX)));


        badFile = new MockMultipartFile(FILE_NAME, ORIGINAL_FILE_NAME,
                XML_TYPE,
                FileUtils.readFileToByteArray(new File(PATH_TO_XML)));

    }

    @Test
    public void controllerShouldSendStatusOkIfFileWasUploadedInDb() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(POST_REQUEST).file(goodFile);
        mockMvc.perform(builder).andExpect(status().isOk());
    }


    @Test
    public void controllerShouldSendBadRequestIfFileHasNotExtensionXlsxOrXls() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(POST_REQUEST).file(badFile);
        mockMvc.perform(builder).andExpect(status().isBadRequest());
    }


    @Test
    public void controllerShouldSendNotFoundWhenDbIsEmpty() throws Exception {
        mockMvc.perform(get(GET_REQUEST))
                .andExpect(status().isNotFound());
    }

    @Test
    public void controllerShouldSendCsv() throws Exception {
        mockMvc.perform(get(GET_REQUEST))
                .andExpect(content().contentType(CSV_CONTENT_TYPE))
                .andExpect(status().isOk());
    }


}
