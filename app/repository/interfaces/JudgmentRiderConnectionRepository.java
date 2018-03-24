package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.JudgmentRiderConnection;
import repository.JudgmentRiderConnectionRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JudgmentRiderConnectionRepositoryImpl.class)
public interface JudgmentRiderConnectionRepository {
    CompletionStage<Stream<JudgmentRiderConnection>> getAllJudgmentRiderConnections();
    CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByRider(int riderId);
    void addJudgmentRiderConnection(JudgmentRiderConnection judgmentRiderConnection);
    void deleteAllJudgmentRiderConnections();
    void deleteJudgmentRiderConnectionByRiderAndJudgmentName(int riderId, String judgmentName);
}
