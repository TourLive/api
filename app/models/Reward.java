package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import models.enums.RewardType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "key_gen_Reward", sequenceName = "key_gen_Reward",  initialValue = 1)
public class Reward {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_Reward")
    private Long id;

    private Long rewardId;
    private RewardType rewardType;
    private String points;
    private String money;

    @OneToMany(mappedBy="reward", cascade= CascadeType.ALL)
    @JsonManagedReference
    private List<Judgment> judgmentRiderConnections = new ArrayList<Judgment>();

    public Long getId() {
        return id;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public ArrayList<Integer> getPoints() {
        String[] m = points.split(",");
        ArrayList<Integer> i = new ArrayList<>();
        for(String s : m){
            i.add(Integer.valueOf(s));
        }
        return i;
    }

    public void setPoints(ArrayList<Integer> points) {
        String s = "";
        for(int i : points){
            s = s.concat(String.valueOf(i)+",");
        }
        this.points = s;
    }

    public ArrayList<Integer> getMoney() {
        String[] m = money.split(",");
        ArrayList<Integer> i = new ArrayList<>();
        for(String s : m){
            i.add(Integer.valueOf(s));
        }
        return i;
    }

    public void setMoney(ArrayList<Integer> money) {
        String s = "";
        for(int i : money){
            s = s.concat(String.valueOf(i)+",");
        }
        this.money = s;
    }

    public List<Judgment> getJudgmentRiderConnections() {
        return judgmentRiderConnections;
    }

    public void setJudgmentRiderConnections(List<Judgment> judgmentRiderConnections) {
        this.judgmentRiderConnections = judgmentRiderConnections;
    }
}
