package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.enums.RewardType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public int rewardId;
    public RewardType rewardType;
    @ElementCollection
    public ArrayList<Integer> points;
    @ElementCollection
    public ArrayList<Integer> money;

    @OneToMany(mappedBy="judgment", cascade= CascadeType.ALL)
    @JsonManagedReference
    public List<Judgment> judgmentRiderConnections = new ArrayList<Judgment>();

    public Long getId() {
        return id;
    }

    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    public ArrayList<Integer> getMoney() {
        return money;
    }

    public void setMoney(ArrayList<Integer> money) {
        this.money = money;
    }

    public List<Judgment> getJudgmentRiderConnections() {
        return judgmentRiderConnections;
    }

    public void setJudgmentRiderConnections(List<Judgment> judgmentRiderConnections) {
        this.judgmentRiderConnections = judgmentRiderConnections;
    }
}
