package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Judgment {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private double distance;

    @OneToMany(mappedBy="judgment", cascade= CascadeType.ALL)
    @JsonManagedReference
    private List<JudgmentRiderConnection> judgmentRiderConnections = new ArrayList<JudgmentRiderConnection>();

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Reward reward;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<JudgmentRiderConnection> getJudgmentRiderConnections() {
        return judgmentRiderConnections;
    }

    public void setJudgmentRiderConnections(List<JudgmentRiderConnection> judgmentRiderConnections) {
        this.judgmentRiderConnections = judgmentRiderConnections;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }
}
