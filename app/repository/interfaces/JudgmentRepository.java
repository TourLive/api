package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Judgment;
import repository.JudgmentRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JudgmentRepositoryImpl.class)
public interface JudgmentRepository {
    CompletionStage<Stream<Judgment>> getAllJudgments();
    CompletionStage<Stream<Judgment>> getJudgmentsByRider(long id);
    CompletionStage<Stream<Judgment>> getJudgmentsByStage(long stageId);
    CompletionStage<Judgment> getJudgmentByIdCompleted(long id);
    Judgment getJudgmentById(long id);
    void addJudgment(Judgment judgment);
    void updateJudgment(Judgment judgment);
    void deleteAllJudgment();
    void deleteJudgmentById(long id);
}
