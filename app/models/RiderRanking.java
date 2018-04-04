package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.enums.RankingType;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "key_gen_RiderRankings", sequenceName = "key_gen_RiderRankings", initialValue = 1)
public class RiderRanking {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_RiderRankings")
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
