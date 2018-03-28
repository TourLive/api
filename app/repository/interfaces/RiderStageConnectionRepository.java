package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.RiderStageConnection;
import repository.RiderStageConnectionRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderStageConnectionRepositoryImpl.class)
public interface RiderStageConnectionRepository {
    CompletionStage<Stream<RiderStageConnection>>  getAllRiderStageConnections();
    CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderAndStage(long stageId, int riderId);
    void addRiderStageConnection(RiderStageConnection riderStageConnection);
    void updateRiderStageConnection(RiderStageConnection riderStageConnection);
    void deleteAllRiderStageConnections();
    void deleteRiderStageConnection(long stageId, int riderId);
}
