package service.impl;

import model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import repository.CsvRepo;
import service.EntityService;

import java.util.List;

/**
 * Implementation of EntityService for Job model by repository
 * Created by Pavel on 24.04.2018.
 */
@Service(value = "EntityServiceImpl")
public class EntityServiceImpl implements EntityService<Job> {

    @Autowired
    @Qualifier("Repo")
    private CsvRepo csvRepo;

    /**
     * Method for searching all Job-object from db
     * @return List of Job-object
     * @see model.Job
     */
    public List<Job> findAll() {
        return csvRepo.findAll();
    }
}
