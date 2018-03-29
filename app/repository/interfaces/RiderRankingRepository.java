package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.RiderRanking;
import models.enums.RankingType;
import repository.RiderRankingRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderRankingRepositoryImpl.class)
public interface RiderRankingRepository {
    CompletionStage<Stream<RiderRanking>> getAllRiderRankings(long riderStageConnectionId);
    CompletionStage<Stream<RiderRanking>> getAllRiderRankingsByType(long riderStageConnectionId, String rankingType);
    CompletionStage<RiderRanking> getRiderRankingByRiderAndType(long riderId, String rankingType);
    void addRiderRanking(RiderRanking riderRanking);
    CompletionStage<RiderRanking> updateRiderRanking(RiderRanking riderRanking);
    void deleteAllRiderRankings();
    void deleteRiderRankingByRiderAndType(long riderId, RankingType rankingType);
}
