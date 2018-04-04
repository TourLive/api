package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Stage;
import repository.StageRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(StageRepositoryImpl.class)
public interface StageRepository {
    CompletionStage<Stream<Stage>> getAllStages();
    CompletionStage<Stage> getStage(long stageId);
    CompletionStage<Stream<Stage>> getAllStagesByRaceId(long raceId);
    void addStage(Stage stage);
    void deleteAllStages();
    void deleteStage(long stageId);
    void updateStage(Stage stage);
}
