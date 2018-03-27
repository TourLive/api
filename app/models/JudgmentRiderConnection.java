package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class JudgmentRiderConnection {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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
}
