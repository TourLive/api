package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.enums.RankingType;

import javax.persistence.*;

@Entity
public class RiderRanking {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private int rank;
    private RankingType rankingType;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private RiderStageConnection riderStageConnection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id;}

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public RankingType getRankingType() {
        return rankingType;
    }

    public void setRankingType(RankingType rankingType) {
        this.rankingType = rankingType;
    }

    public RiderStageConnection getRiderStageConnection() {
        return riderStageConnection;
    }

    public void setRiderStageConnection(RiderStageConnection riderStageConnection) {
        this.riderStageConnection = riderStageConnection;
    }
}
