package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public int riderId;
    public int startNr;
    public String name;
    public String country;
    public String teamName;
    public String teamShortName;
    public boolean isUnkown;

    @OneToMany(mappedBy="rider", cascade= CascadeType.ALL)
    @JsonManagedReference
    public List<RiderStageConnection> riderStageConnections = new ArrayList<RiderStageConnection>();
    @OneToMany(mappedBy="rider", cascade= CascadeType.ALL)
    @JsonManagedReference
    public List<JudgmentRiderConnection> judgmentRiderConnections = new ArrayList<JudgmentRiderConnection>();
    @ManyToMany(mappedBy="riders")
    @JsonManagedReference
    public List<RaceGroup> raceGroups = new ArrayList<RaceGroup>();

    public Long getId() {
        return id;
    }

    public int getRiderId() {
        return riderId;
    }

    public void setRiderId(int riderId) {
        this.riderId = riderId;
    }

    public int getStartNr() {
        return startNr;
    }

    public void setStartNr(int startNr) {
        this.startNr = startNr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamShortName() {
        return teamShortName;
    }

    public void setTeamShortName(String teamShortName) {
        this.teamShortName = teamShortName;
    }

    public boolean isUnkown() {
        return isUnkown;
    }

    public void setUnkown(boolean unkown) {
        isUnkown = unkown;
    }

    public List<RiderStageConnection> getRiderStageConnections() {
        return riderStageConnections;
    }

    public void setRiderStageConnections(List<RiderStageConnection> riderStageConnections) {
        this.riderStageConnections = riderStageConnections;
    }

    public List<JudgmentRiderConnection> getJudgmentRiderConnections() {
        return judgmentRiderConnections;
    }

    public void setJudgmentRiderConnections(List<JudgmentRiderConnection> judgmentRiderConnections) {
        this.judgmentRiderConnections = judgmentRiderConnections;
    }

    public List<RaceGroup> getRaceGroups() {
        return raceGroups;
    }

    public void setRaceGroups(List<RaceGroup> raceGroups) {
        this.raceGroups = raceGroups;
    }
}
