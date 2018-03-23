package repository.interfaces;

import models.RiderRanking;
import models.enums.RankingType;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface RiderRankingRepository {
    CompletionStage<Stream<RiderRanking>> getAllRiderRankings();
    CompletionStage<Stream<RiderRanking>> getAllRiderRankingsByType(RankingType rankingType);
    CompletionStage<RiderRanking> getRankingByRiderAndType(int riderId, RankingType rankingType);
    void addRiderRanking(CompletionStage<RiderRanking> riderRanking);
    void updateRiderRanking(CompletionStage<RiderRanking> riderRanking);
    void deleteAllRiderRankings();
    void deleteRiderRankingByRiderAndType(int riderId, RankingType rankingType);
}
