package models;

import models.enums.RaceGroupType;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity @Access(AccessType.PROPERTY)
public class RaceGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public RaceGroupType raceGroupType;
    public long actualGapTime;
    public long historyGapTime;
    public int position;
    public Timestamp timestamp;

    @OneToMany(mappedBy="raceGroup", cascade= CascadeType.ALL)
    public List<RiderRaceGroup> riderRaceGroups = new ArrayList<RiderRaceGroup>();

    public Long getId() {
        return id;
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

    public List<RiderRaceGroup> getRiderRaceGroups() {
        return riderRaceGroups;
    }

    public void setRiderRaceGroups(List<RiderRaceGroup> riderRaceGroups) {
        this.riderRaceGroups = riderRaceGroups;
    }
}
