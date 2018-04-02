package repository;

import model.Weights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Pavel on 30.03.2018.
 */

/**
 * Repository for queries on db
 */
@Repository("Repo")
@Transactional
public interface CsvRepo extends JpaRepository<Weights, Long> {


    /**
     * Overriding a method findAll() of existing realization(not required) by SpEL
     * @return a list of Weights from db
     */
    @Modifying
    @Query("SELECT w from Weights w")
    List<Weights> findAll();


    /**
     * Overriding a method save() of existing realization(required) by native query
     * @param word word from .csv file
     * @param str_value him value
     */
    @Modifying
    @Query(value = "INSERT INTO csv.weights(word, str_value) VALUES(?1, ?2)", nativeQuery = true)
    void save(String word, BigDecimal str_value);

}
