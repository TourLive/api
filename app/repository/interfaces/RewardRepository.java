package repository.interfaces;

import models.Reward;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface RewardRepository {
    CompletionStage<Stream<Reward>> getAllRewards();
    void addReward(CompletionStage<Reward> reward);
    void deleteAllRewards();
}
