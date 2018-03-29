package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Judgment;
import repository.JudgmentRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(JudgmentRepositoryImpl.class)
public interface JudgmentRepository {
    Stream<Judgment> getAllJudgments();
    Stream<Judgment> getJudgmentsByRider(long id);
    void addJudgment(Judgment judgment);
    void deleteAllJudgment();
    void deleteJudgmentById(long id);
}
