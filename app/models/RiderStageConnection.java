package models;

import models.enums.StageType;
import models.enums.TypeState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Access(AccessType.PROPERTY)
public class RiderStageConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public int bonusPoints;
    public int mountainBonusPoints;
    public int sprintBonusPoints;
    public int bonusTime;
    public int money;
    public Long officialTime;
    public Long officialGap;
    public Long virtualGap;
    public TypeState typeState;

    @ManyToOne(cascade=CascadeType.PERSIST)
    public Stage stage;
    @OneToMany(mappedBy="riderStageConnection", cascade= CascadeType.ALL)
    public List<RiderRanking> riderRankings = new ArrayList<RiderRanking>();
    @ManyToOne(cascade=CascadeType.PERSIST)
    public Rider rider;

    public Long getId() {
        return id;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public int getMountainBonusPoints() {
        return mountainBonusPoints;
    }

    public void setMountainBonusPoints(int mountainBonusPoints) {
        this.mountainBonusPoints = mountainBonusPoints;
    }

    public int getSprintBonusPoints() {
        return sprintBonusPoints;
    }

    public void setSprintBonusPoints(int sprintBonusPoints) {
        this.sprintBonusPoints = sprintBonusPoints;
    }

    public int getBonusTime() {
        return bonusTime;
    }

    public void setBonusTime(int bonusTime) {
        this.bonusTime = bonusTime;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Long getOfficialTime() {
        return officialTime;
    }

    public void setOfficialTime(Long officialTime) {
        this.officialTime = officialTime;
    }

    public Long getOfficialGap() {
        return officialGap;
    }

    public void setOfficialGap(Long officialGap) {
        this.officialGap = officialGap;
    }

    public Long getVirtualGap() {
        return virtualGap;
    }

    public void setVirtualGap(Long virtualGap) {
        this.virtualGap = virtualGap;
    }

    public TypeState getTypeState() {
        return typeState;
    }

    public void setTypeState(TypeState typeState) {
        this.typeState = typeState;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public List<RiderRanking> getRiderRankings() {
        return riderRankings;
    }

    public void setRiderRankings(List<RiderRanking> riderRankings) {
        this.riderRankings = riderRankings;
    }
}
