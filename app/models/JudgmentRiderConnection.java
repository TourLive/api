package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "key_gen_JudgmentRiderConnection", sequenceName = "key_gen_JudgmentRiderConnection",  initialValue = 1)
public class JudgmentRiderConnection {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_JudgmentRiderConnection")
    private Long id;
    private int rank;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Rider rider;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Judgment judgment;

    public Long getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public Judgment getJudgment() {
        return judgment;
    }

    public void setJudgment(Judgment judgment) {
        this.judgment = judgment;
    }

}
