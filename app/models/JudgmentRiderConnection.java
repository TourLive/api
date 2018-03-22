package models;

import javax.persistence.*;

@Entity @Access(AccessType.PROPERTY)
public class JudgmentRiderConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public int rank;

    @ManyToOne(cascade=CascadeType.PERSIST)
    public Rider rider;

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
