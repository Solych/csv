package model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Pavel on 20.04.2018.
 */
@Entity
@Table(name = "job", schema = "TIMETABLE")
public class Job {
    @Id
    @SequenceGenerator(name = "seq", allocationSize = 1, sequenceName = "timeSeq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Column(name = "ID")
    private Long id;

    @Column(name = "DISCIPLINE", length = 50)
    private String discipline;

    @Column(name = "DATE_TIME")
    private Date dateTime;

    @Column(name = "GROUP_NAME", length = 20)
    private String groupName;

    @Column(name = "ROOM", length = 100)
    private String room;

    public Job(String discipline, Date dateTime, String groupName, String room) {
        this.discipline = discipline;
        this.dateTime = dateTime;
        this.groupName = groupName;
        this.room = room;
    }

    public Job(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
