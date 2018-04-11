package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "key_gen_Judgment", sequenceName = "key_gen_Judgment",  initialValue = 1)
public class Judgment {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_Judgment")
    private Long id;
    @JsonIgnore
    private Long cnlabStageId;
    private String name;
    private double distance;

    @OneToMany(mappedBy="judgment", cascade= CascadeType.ALL)
    @JsonBackReference
    private List<JudgmentRiderConnection> judgmentRiderConnections = new ArrayList<>();

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonManagedReference
    private Reward reward;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Stage stage;

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

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Long getcnlabStageId() {
        return cnlabStageId;
    }

    public void setcnlabStageId(Long cnlabStageId) {
        this.cnlabStageId = cnlabStageId;
    }
}
