package repository;

import model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Pavel on 30.03.2018.
 */

/**
 * Repository for queries on db
 */
@Repository("Repo")
public interface CsvRepo extends JpaRepository<Job, Long> {

    List<Job> findAll();


}
