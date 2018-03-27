package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.RiderStageConnection;
import repository.RiderStageConnectionRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderStageConnectionRepositoryImpl.class)
public interface RiderStageConnectionRepository {
    CompletionStage<JsonNode>  getAllRiderStageConnections();
    CompletionStage<JsonNode>  getRiderStageConnectionByRiderAndStage(int riderId, int stageId);
    CompletionStage<JsonNode>  addRiderStageConnection(RiderStageConnection riderStageConnection);
    CompletionStage<JsonNode>  updateRiderStageConnection(RiderStageConnection riderStageConnection);
    CompletionStage<JsonNode>  deleteAllRiderStageConnections();
    CompletionStage<JsonNode>  deleteRiderStageConnection(int riderId, int stageId);
}
