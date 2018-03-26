package models;

import models.enums.StageType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public int stageId;
    public Date startTime;
    public Date endTime;
    public int distance;
    public StageType stageType;
    public String from2;
    public String to2;

    @ManyToOne(cascade=CascadeType.PERSIST)
    public Race race;
    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    public List<RiderStageConnection> riderStageConnections = new ArrayList<RiderStageConnection>();
    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    public List<Maillot> mailllots = new ArrayList<Maillot>();
    @OneToMany(mappedBy="stage", cascade= CascadeType.ALL)
    public List<Notification> notifications = new ArrayList<Notification>();

    public Long getId() {
        return id;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
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

    public String getFrom() {
        return from2;
    }

    public void setFrom(String from) {
        this.from2 = from;
    }

    public String getTo() {
        return to2;
    }

    public void setTo(String to) {
        this.to2 = to;
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
