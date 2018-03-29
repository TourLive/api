package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.RiderStageConnection;
import repository.RiderStageConnectionRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderStageConnectionRepositoryImpl.class)
public interface RiderStageConnectionRepository {
    CompletionStage<Stream<RiderStageConnection>>  getAllRiderStageConnections(long stageId);
    CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderAndStage(long stageId, long riderId);
    void addRiderStageConnection(RiderStageConnection riderStageConnection);
    CompletionStage<RiderStageConnection> updateRiderStageConnection(RiderStageConnection riderStageConnection);
    void deleteAllRiderStageConnections();
    void deleteRiderStageConnection(long stageId, long riderId);
}
