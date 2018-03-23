package models;

import javax.persistence.*;

@Entity @Access(AccessType.PROPERTY)
public class RiderRaceGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @ManyToOne(cascade=CascadeType.PERSIST)
    public Rider rider;
    @ManyToOne(cascade=CascadeType.PERSIST)
    public RaceGroup raceGroup;

    public Long getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public RaceGroup getRaceGroup() {
        return raceGroup;
    }

    public void setRaceGroup(RaceGroup raceGroup) {
        this.raceGroup = raceGroup;
    }
}
