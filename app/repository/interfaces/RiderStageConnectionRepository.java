package repository.interfaces;

import models.RiderStageConnection;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface RiderStageConnectionRepository {
    CompletionStage<Stream<RiderStageConnection>> getAllRiderStageConnections();
    void addRiderStageConnection(CompletionStage<RiderStageConnection> riderStageConnection);
    CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderAndStage(int riderId, int stageId);
    void deleteAllRiderStageConnections();
    void deleteRiderStageConnection(int riderId, int stageId);
}
