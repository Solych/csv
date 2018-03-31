package repository;

import model.Weights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Pavel on 30.03.2018.
 */
@Repository("Repo")
@Transactional
public interface CsvRepo extends JpaRepository<Weights, Long> {
    //findall - exist
    // save - exist

    @Modifying
    @Query("SELECT w from Weights w")
    List<Weights> findAll();

    @Modifying
    @Query(value = "INSERT INTO csv.weights(word, str_value) VALUES(?1, ?2)", nativeQuery = true)
    void save(String word, BigDecimal str_value);

}
