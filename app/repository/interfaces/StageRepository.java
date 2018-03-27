package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.Stage;
import repository.StageRepositoryImpl;

import java.util.stream.Stream;

import java.util.concurrent.CompletionStage;

@ImplementedBy(StageRepositoryImpl.class)
public interface StageRepository {
    CompletionStage<Stream<Stage>> getAllStages();
    CompletionStage<Stage> getStage(long stageId);
    CompletionStage<Stage> addStage(Stage stage);
    CompletionStage<Stream<Stage>> deleteAllStages();
    CompletionStage<Stage> deleteStage(long stageId);
}
