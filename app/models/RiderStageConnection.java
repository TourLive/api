package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.enums.StageType;
import models.enums.TypeState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RiderStageConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int bonusPoints;
    private int mountainBonusPoints;
    private int sprintBonusPoints;
    private int bonusTime;
    private int money;
    private Long officialTime;
    private Long officialGap;
    private Long virtualGap;
    private TypeState typeState;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Stage stage;
    @OneToMany(mappedBy="riderStageConnection", cascade= CascadeType.ALL)
    @JsonManagedReference
    private List<RiderRanking> riderRankings = new ArrayList<RiderRanking>();
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Rider rider;

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
