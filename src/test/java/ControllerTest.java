import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.io.File;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Pavel on 04.04.2018.
 */
@Transactional
@DirtiesContext
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {config.DbConfigTest.class, config.AppInit.class, config.StartApp.class})
@WebAppConfiguration
public class ControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;


    /**
     * Test for extension of file - if xlsx - status.ok, else - bad request
     * @throws Exception
     */
    @Test
    public void testLoad() throws Exception{

        MockMultipartFile goodFile = new MockMultipartFile
                ("file","excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//excel.xlsx")));


        MockMultipartFile badFile = new MockMultipartFile("file","pom",
                "application/xml",
                FileUtils.readFileToByteArray(new File("Z://JavaProject//csv//pom.xml")));
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(goodFile)).
                andExpect(status().is(200));


        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload").file(badFile))
                .andExpect(status().is(400));
    }
}
