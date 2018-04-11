package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "key_gen_Settings", sequenceName = "key_gen_Settings",  initialValue = 1)
public class Setting {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator = "key_gen_Settings")
    @JsonIgnore
    private Long id;

    private Long raceID;
    private Long stageID;

    public Long getRaceID() {
        return raceID;
    }

    public void setRaceID(Long raceID) {
        this.raceID = raceID;
    }

    public Long getStageID() {
        return stageID;
    }

    public void setStageID(Long stageID) {
        this.stageID = stageID;
    }
}