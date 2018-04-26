package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.enums.StageType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@SequenceGenerator(name = "key_gen_Stage", sequenceName = "key_gen_Stage",  initialValue = 1)
@ApiModel(value = "Stage", description="Model of stage")
public class Stage {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_Stage")
    private Long id;

    private Long stageId;
    private Date startTime;
    private Date endTime;
    private double distance;
    private StageType stageType;
    private String start;
    private String destination;
    private String stageName;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private Race race;

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private List<RiderStageConnection> riderStageConnections = new ArrayList<>();

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonIgnore
    @ApiModelProperty(hidden=true)
    private transient List<RaceGroup> racegroups = new ArrayList<>();

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonIgnore
    @ApiModelProperty(hidden=true)
    private List<Maillot> mailllots = new ArrayList<>();

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonIgnore
    @ApiModelProperty(hidden=true)
    private List<Judgment> judgments = new ArrayList<>();

    public Long getId() {
        return id;
    }
   
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType = stageType;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @ApiModelProperty(hidden=true)
    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    @ApiModelProperty(hidden=true)
    public List<RiderStageConnection> getRiderStageConnections() {
        return riderStageConnections;
    }

    public void setRiderStageConnections(List<RiderStageConnection> riderStageConnections) {
        this.riderStageConnections = riderStageConnections;
    }

    @ApiModelProperty(hidden=true)
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @ApiModelProperty(hidden=true)
    public List<RaceGroup> getRaceGroups() {
        return racegroups;
    }

    public void setRaceGroups(List<RaceGroup> racegroups) {
        this.racegroups = racegroups;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    @ApiModelProperty(hidden=true)
    public List<Maillot> getMailllots() {
        return mailllots;
    }

    public void setMailllots(List<Maillot> mailllots) {
        this.mailllots = mailllots;
    }

    public List<Judgment> getJudgments() {
        return judgments;
    }

    @ApiModelProperty(hidden=true)
    public void setJudgments(List<Judgment> judgments) {
        this.judgments = judgments;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
}
