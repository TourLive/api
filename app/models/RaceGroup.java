package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.enums.RaceGroupType;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "key_gen_RaceGroup", sequenceName = "key_gen_RaceGroup",  initialValue = 1)
public class RaceGroup {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_RaceGroup")
    private Long id;
    private RaceGroupType raceGroupType;
    private long actualGapTime;
    private long historyGapTime;
    private int position;
    private Timestamp timestamp;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonIgnore
    private Stage stage;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Rider> riders = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RaceGroupType getRaceGroupType() {
        return raceGroupType;
    }

    public void setRaceGroupType(RaceGroupType raceGroupType) {
        this.raceGroupType = raceGroupType;
    }

    public long getActualGapTime() {
        return actualGapTime;
    }

    public void setActualGapTime(long actualGapTime) {
        this.actualGapTime = actualGapTime;
    }

    public long getHistoryGapTime() {
        return historyGapTime;
    }

    public void setHistoryGapTime(long historyGapTime) {
        this.historyGapTime = historyGapTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public List<Rider> getRiders() {
        return riders;
    }

    public void setRiders(List<Rider> riders) {
        this.riders = riders;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
