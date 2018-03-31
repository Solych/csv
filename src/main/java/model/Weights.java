package model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Pavel on 29.03.2018.
 */
@Entity
@Table(name = "Weights", schema = "csv")
public class Weights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "csv.csvSeq")
    @Column(name = "string_id")
    private Integer string_id;

    @Column(name = "word", unique = true)
    private String word;

    @Column(name = "str_value")
    private BigDecimal str_value;

    public Weights(String word, BigDecimal str_value) {
        this.word = word;
        this.str_value = str_value;
    }

    public Integer getString_id() {
        return string_id;
    }

    public void setString_id(Integer string_id) {
        this.string_id = string_id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public BigDecimal getStr_value() {
        return str_value;
    }

    public void setStr_value(BigDecimal str_value) {
        this.str_value = str_value;
    }


    public Weights() {

    }
}
