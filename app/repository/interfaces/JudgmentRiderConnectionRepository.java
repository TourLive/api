package repository.interfaces;

import models.JudgmentRiderConnection;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface JudgmentRiderConnectionRepository {
    CompletionStage<Stream<JudgmentRiderConnection>> getAllJudgmentRiderConnections();
    CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByRider(int riderId);
    void addJudgmentRiderConnection(CompletionStage<JudgmentRiderConnection> judgmentRiderConnection);
    void deleteAllJudgmentRiderConnections();
    void deleteJudgmentRiderConnectionByRiderAndJudgmentName(int riderId, String judgmentName);
}
