package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Reward;
import repository.RewardRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RewardRepositoryImpl.class)
public interface RewardRepository {
    Stream<Reward> getAllRewards();
    Reward getRewardById(long id);
    void addReward(Reward reward);
    void deleteAllRewards();
}
