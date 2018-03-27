package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.Stage;
import repository.StageRepositoryImpl;

import java.util.stream.Stream;

import java.util.concurrent.CompletionStage;

@ImplementedBy(StageRepositoryImpl.class)
public interface StageRepository {
    CompletionStage<JsonNode> getAllStages();
    CompletionStage<JsonNode> getStage(int stageId);
    CompletionStage<JsonNode> addStage(Stage stage);
    CompletionStage<JsonNode> deleteAllStages();
    CompletionStage<JsonNode> deleteStage(int stageId);
}
