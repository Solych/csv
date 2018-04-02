package model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Model with hibernate markup
 */
@Entity
@Table(name = "Weights", schema = "CSV")
public class Weights {

    @Id
    @SequenceGenerator(name = "seq", sequenceName = "csvSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
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
