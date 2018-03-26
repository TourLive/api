package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Stage;
import repository.StageRepositoryImpl;

import java.util.stream.Stream;

import java.util.concurrent.CompletionStage;

@ImplementedBy(StageRepositoryImpl.class)
public interface StageRepository {
    CompletionStage<Stream<Stage>> getAllStages();
    CompletionStage<Stage> getStage(int stageId);
    void addStage(CompletionStage<Stage> stage);
    void deleteAllStage();
    void deleteStage(int stageId);
}
