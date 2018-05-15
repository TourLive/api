package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.enums.RankingType;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "key_gen_RiderRankings", sequenceName = "key_gen_RiderRankings", initialValue = 1)
@ApiModel(value = "RiderRanking", description="Model of rider ranking")
public class RiderRanking {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_RiderRankings")
    private Long id;
    private int rank;
    private RankingType rankingType;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
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

    @ApiModelProperty(hidden=true)
    public RiderStageConnection getRiderStageConnection() {
        return riderStageConnection;
    }

    public void setRiderStageConnection(RiderStageConnection riderStageConnection) {
        this.riderStageConnection = riderStageConnection;
    }
}
