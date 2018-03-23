package repository.interfaces;

import models.RiderStageConnection;
import models.Stage;
import java.util.stream.Stream;

import java.util.concurrent.CompletionStage;

public interface StageRepository {
    CompletionStage<Stream<Stage>> getAllStages();
    void addStage(CompletionStage<Stage> stage);
    CompletionStage<Stage> getStage(int stageId);
    void updateRiderStageConnection(CompletionStage<RiderStageConnection> riderStageConnection);
    void deleteAllStage();
    void deleteStage(int stageId);
}
