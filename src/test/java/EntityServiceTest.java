import config.EmbeddedDbDataSource;
import config.WebAppContext;
import model.Job;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import repository.CsvRepo;

import java.util.List;

import static org.mockito.Mockito.mock;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebAppContext.class)
@WebAppConfiguration
public class EntityServiceTest {

    @Before
    public void setUp() {

    }

    @Autowired
    @Qualifier("Repo")
    private CsvRepo csvRepo;

    @Test
    public void shouldEqualsZeroSizeIfDbIsEmpty() {
        List<Job> jobs = csvRepo.findAll();
        Assert.assertEquals(jobs.size(), 0);
    }

}
