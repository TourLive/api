package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.RiderRanking;
import models.enums.RankingType;
import repository.RiderRankingRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderRankingRepositoryImpl.class)
public interface RiderRankingRepository {
    CompletionStage<Stream<RiderRanking>> getAllRiderRankings();
    CompletionStage<Stream<RiderRanking>> getAllRiderRankingsByType(RankingType rankingType);
    CompletionStage<RiderRanking> getRankingByRiderAndType(int riderId, RankingType rankingType);
    void addRiderRanking(RiderRanking riderRanking);
    void updateRiderRanking(RiderRanking riderRanking);
    void deleteAllRiderRankings();
    void deleteRiderRankingByRiderAndType(int riderId, RankingType rankingType);
}
