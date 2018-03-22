package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Access(AccessType.PROPERTY)
public class Judgment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String name;
    public int distance;

    @OneToMany(mappedBy="judgment", cascade= CascadeType.ALL)
    public List<JudgmentRiderConnection> judgmentRiderConnections = new ArrayList<JudgmentRiderConnection>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public List<JudgmentRiderConnection> getJudgmentRiderConnections() {
        return judgmentRiderConnections;
    }

    public void setJudgmentRiderConnections(List<JudgmentRiderConnection> judgmentRiderConnections) {
        this.judgmentRiderConnections = judgmentRiderConnections;
    }
}
