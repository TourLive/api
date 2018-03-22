package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Access(AccessType.PROPERTY)
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public int riderId;
    public int startNr;
    public String name;
    public String country;
    public String teamName;
    public String teamShortName;
    public boolean isUnkown;

    @OneToMany(mappedBy="riderStageConnection", cascade= CascadeType.ALL)
    public List<RiderStageConnection> riderStageConnections = new ArrayList<RiderStageConnection>();

    public Long getId() {
        return id;
    }

    public int getRiderId() {
        return riderId;
    }

    public void setRiderId(int riderId) {
        this.riderId = riderId;
    }

    public int getStartNr() {
        return startNr;
    }

    public void setStartNr(int startNr) {
        this.startNr = startNr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamShortName() {
        return teamShortName;
    }

    public void setTeamShortName(String teamShortName) {
        this.teamShortName = teamShortName;
    }

    public boolean isUnkown() {
        return isUnkown;
    }

    public void setUnkown(boolean unkown) {
        isUnkown = unkown;
    }

    public List<RiderStageConnection> getRiderStageConnections() {
        return riderStageConnections;
    }

    public void setRiderStageConnections(List<RiderStageConnection> riderStageConnections) {
        this.riderStageConnections = riderStageConnections;
    }
}
