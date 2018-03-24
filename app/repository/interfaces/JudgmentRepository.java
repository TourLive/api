package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Judgment;
import repository.JudgmentRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JudgmentRepositoryImpl.class)
public interface JudgmentRepository {
    CompletionStage<Stream<Judgment>> getAllJudgments();
    CompletionStage<Stream<Judgment>> getJudgmentsByRider(int riderId);
    void addJudgment(Judgment judgment);
    void deleteAllJudgment();
    void deleteJudgmentByJudgmentName(String judgmentName);
}
