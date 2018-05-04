import config.WebAppContext;
import model.Job;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import repository.CsvRepo;

import java.util.List;


/**
 * Created by Pavel on 04.05.2018.
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = WebAppContext.class)
public class EntityServiceTest {
    @Autowired
    @Qualifier("Repo")
    private CsvRepo csvRepo;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEIfDbIsEmpty(){
        List<Job> jobs = csvRepo.findAll();
        jobs.get(0);

    }

}
