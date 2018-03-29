package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Date startTime;
    private Date endTime;
    private int distance;
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
    private List<Maillot> mailllots = new ArrayList<Maillot>();
    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    @JsonBackReference
    private List<Notification> notifications = new ArrayList<Notification>();

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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
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

    public List<Maillot> getMailllots() {
        return mailllots;
    }

    public void setMailllots(List<Maillot> mailllots) {
        this.mailllots = mailllots;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
