package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.enums.TypeState;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "key_gen_RiderStageConnections", sequenceName = "key_gen_RiderStageConnections", initialValue = 1)
@ApiModel(value = "RiderStageConnection", description="Model of rider stage connection")
public class RiderStageConnection {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_RiderStageConnections")
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
    @ApiModelProperty(hidden=true)
    private Stage stage;
    @OneToMany(mappedBy="riderStageConnection", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<RiderRanking> riderRankings = new ArrayList<>();
    @ManyToOne
    @JsonManagedReference
    private Rider rider;
    @ManyToMany(cascade= CascadeType.ALL)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private List<Maillot> riderMaillots = new ArrayList<>();

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

    @ApiModelProperty(hidden=true)
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

    @ApiModelProperty(hidden=true)
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
