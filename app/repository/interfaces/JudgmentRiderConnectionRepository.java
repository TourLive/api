package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.JudgmentRiderConnection;
import repository.JudgmentRiderConnectionRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JudgmentRiderConnectionRepositoryImpl.class)
public interface JudgmentRiderConnectionRepository {
    CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByRider(long id);
    CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByStage(long stageId);
    CompletionStage<JudgmentRiderConnection> addJudgmentRiderConnection(JudgmentRiderConnection judgmentRiderConnection);
    void deleteAllJudgmentRiderConnections();
    CompletionStage<JudgmentRiderConnection> deleteJudgmentRiderConnection(String judgmentId);
    Stream<JudgmentRiderConnection> getAllJudgmentRiderConnections();
}
