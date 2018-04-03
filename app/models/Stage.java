package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.enums.StageType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Stage {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private Long stageId;
    private Date startTime;
    private Date endTime;
    private double distance;
    private StageType stageType;
    private String start;
    private String destination;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Race race;

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonBackReference
    private List<RiderStageConnection> riderStageConnections = new ArrayList<RiderStageConnection>();

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonBackReference
    private List<Notification> notifications = new ArrayList<Notification>();

    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonIgnore
    private transient List<RaceGroup> racegroups = new ArrayList<RaceGroup>();

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

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public List<RiderStageConnection> getRiderStageConnections() {
        return riderStageConnections;
    }

    public void setRiderStageConnections(List<RiderStageConnection> riderStageConnections) {
        this.riderStageConnections = riderStageConnections;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

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
}
