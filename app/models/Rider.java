package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "key_gen_Rider", sequenceName = "key_gen_Rider",  initialValue = 1)
@ApiModel(value = "Rider", description="Model of rider")
public class Rider {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_Rider")
    private Long id;

    private Long riderId;
    private int startNr;
    private String name;
    private String country;
    private String teamName;
    private String teamShortName;
    private boolean isUnknown;

    @OneToMany(mappedBy="rider", cascade= CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private List<RiderStageConnection> riderStageConnections = new ArrayList<>();
    @OneToMany(mappedBy="rider", cascade= CascadeType.ALL)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private List<JudgmentRiderConnection> judgmentRiderConnections = new ArrayList<>();
    @ManyToMany(mappedBy="riders", cascade= CascadeType.MERGE)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private List<RaceGroup> raceGroups = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
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

    public boolean isUnknown() {
        return isUnknown;
    }

    public void setUnknown(boolean unknown) {
        isUnknown = unknown;
    }

    @ApiModelProperty(hidden=true)
    public List<RiderStageConnection> getRiderStageConnections() {
        return riderStageConnections;
    }

    public void setRiderStageConnections(List<RiderStageConnection> riderStageConnections) {
        this.riderStageConnections = riderStageConnections;
    }

    @ApiModelProperty(hidden=true)
    public List<JudgmentRiderConnection> getJudgmentRiderConnections() {
        return judgmentRiderConnections;
    }

    public void setJudgmentRiderConnections(List<JudgmentRiderConnection> judgmentRiderConnections) {
        this.judgmentRiderConnections = judgmentRiderConnections;
    }

    @ApiModelProperty(hidden=true)
    public List<RaceGroup> getRaceGroups() {
        return raceGroups;
    }

    public void setRaceGroups(List<RaceGroup> raceGroups) {
        this.raceGroups = raceGroups;
    }
}
