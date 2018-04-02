package service;

import model.Weights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import repository.CsvRepo;

import java.util.List;

/**
 * Created by Pavel on 30.03.2018.
 */
@Service(value = "RepoImpl")
@ComponentScan(basePackageClasses = repository.CsvRepo.class)
public class CsvRepoImpl {


    @Autowired
    @Qualifier("Repo")
    private CsvRepo csvRepo;


    public List<Weights> findAll() {
        List<Weights> weights = csvRepo.findAll();
        if (weights.size() == 0)
            System.out.println("not found in a bd any weights");
        return weights;
    }


    public void save(List<Weights> weights){

        if(weights.size() != 0){
            for(Weights weights1: weights)
                csvRepo.save(weights1.getWord(), weights1.getStr_value());
        } else
            System.out.println("weights is empty");
    }
}
