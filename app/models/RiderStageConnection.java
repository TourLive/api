package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.enums.TypeState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RiderStageConnection {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private int bonusPoints;
    private int mountainBonusPoints;
    private int sprintBonusPoints;
    private int bonusTime;
    private int money;
    private long officialTime;
    private long officialGap;
    private long virtualGap;
    private TypeState typeState;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Stage stage;
    @OneToMany(mappedBy="riderStageConnection", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<RiderRanking> riderRankings = new ArrayList<RiderRanking>();
    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonManagedReference
    private Rider rider;
    @ManyToMany(cascade= CascadeType.ALL)
    @JsonManagedReference
    private List<Maillot> riderMaillots = new ArrayList<Maillot>();

    public Long getId() {
        return id;
    }

    public void setId(long id) {this.id = id;}

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

    public long getOfficialTime() {
        return officialTime;
    }

    public void setOfficialTime(long officialTime) {
        this.officialTime = officialTime;
    }

    public long getOfficialGap() {
        return officialGap;
    }

    public void setOfficialGap(long officialGap) {
        this.officialGap = officialGap;
    }

    public long getVirtualGap() {
        return virtualGap;
    }

    public void setVirtualGap(long virtualGap) {
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

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public List<Maillot> getRiderMaillots() {
        return riderMaillots;
    }

    public void setRiderMaillots(List<Maillot> riderMaillots) {
        this.riderMaillots = riderMaillots;
    }

    public void addRiderMaillots(Maillot riderMaillot) {
        this.riderMaillots.add(riderMaillot);
    }
}
