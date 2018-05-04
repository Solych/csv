
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


    @Before
    public void setMockMvc() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        goodFile = new MockMultipartFile
                ("file", "excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//timeTable.xlsx")));


        badFile = new MockMultipartFile("file", "pom",
                "application/xml",
                FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//pom.xml")));

    }

    @Test
    public void controllerShouldSendStatusOkIfFileWasUploadedInDb() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(goodFile))
                .andExpect(status().isOk());
    }


    @Test
    public void controllerShouldSendBadRequestIfFileHasNotExtensionXlsxOrXls() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(badFile))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void controllerShouldSendNotFoundWhenDbIsEmpty() throws Exception {
        mockMvc.perform(get("/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void controllerShouldSendCsv() throws Exception {
        mockMvc.perform(get("/download"))
                .andExpect(content().contentType("application/octet-stream"))
                .andExpect(status().isOk());
    }


}
