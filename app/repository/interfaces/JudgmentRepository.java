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
    CompletionStage<Judgment> addJudgment(Judgment judgment);
    CompletionStage<Stream<Judgment>> deleteAllJudgment();
    CompletionStage<Judgment> deleteJudgmentById(long id);
}
