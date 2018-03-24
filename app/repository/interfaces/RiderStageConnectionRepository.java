package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.RiderStageConnection;
import repository.RiderStageConnectionRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderStageConnectionRepositoryImpl.class)
public interface RiderStageConnectionRepository {
    CompletionStage<Stream<RiderStageConnection>> getAllRiderStageConnections();
    CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderAndStage(int riderId, int stageId);
    void addRiderStageConnection(RiderStageConnection riderStageConnection);
    void deleteAllRiderStageConnections();
    void deleteRiderStageConnection(int riderId, int stageId);
}
