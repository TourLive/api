package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Rider {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private int startNr;
    private String name;
    private String country;
    private String teamName;
    private String teamShortName;
    private boolean isUnknown;

    @OneToMany(mappedBy="rider", cascade= CascadeType.ALL)
    @JsonManagedReference
    private List<RiderStageConnection> riderStageConnections = new ArrayList<RiderStageConnection>();
    @OneToMany(mappedBy="rider", cascade= CascadeType.ALL)
    @JsonManagedReference
    private List<JudgmentRiderConnection> judgmentRiderConnections = new ArrayList<JudgmentRiderConnection>();
    @ManyToMany(mappedBy="riders")
    @JsonManagedReference
    private List<RaceGroup> raceGroups = new ArrayList<RaceGroup>();

    public Long getId() {
        return id;
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
        return isUnknown;
    }

    public void setUnkown(boolean unknown) {
        isUnknown = unknown;
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
