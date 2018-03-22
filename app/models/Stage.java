package models;

import models.enums.StageType;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public int stageId;
    public int raceId;
    public String raceName;
    public Date startTime;
    public Date endTime;
    public int distance;
    public StageType stageType;
    public String from;
    public String to;
    @ManyToOne(cascade=CascadeType.PERSIST)
    public Race race;
}
