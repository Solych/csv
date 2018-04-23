package model;

import javax.persistence.*;

/**
 * Created by Pavel on 20.04.2018.
 */
@Entity
@Table(name = "job", schema = "TIMETABLE")
public class Job {
    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "DISCIPLINE", length = 60)
    private String discipline;

    @Column(name = "DATE_TIME", length = 50)
    private String dateTime;

    @Column(name = "GROUP_NAME", length = 30)
    private String groupName;

    @Column(name = "ROOM", length = 120)
    private String room;

    public Job(Long id,String discipline, String dateTime, String groupName, String room) {
        this.id = id;
        this.discipline = discipline;
        this.dateTime = dateTime;
        this.groupName = groupName;
        this.room = room;
    }

    public Job() {

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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
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
