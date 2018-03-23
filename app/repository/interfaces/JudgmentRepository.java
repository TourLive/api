package repository.interfaces;

import models.Judgment;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface JudgmentRepository {
    CompletionStage<Stream<Judgment>> getAllJudgments();
    CompletionStage<Stream<Judgment>> getJudgmentsByRider(int riderId);
    void addJudgment(CompletionStage<Judgment> judgment);
    void deleteAllJudgment();
    void deleteJudgmentByJudgmentName(String judgmentName);
}
