package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Log;
import models.Notification;
import models.enums.NotificationType;
import repository.LogRepositoryImpl;
import repository.NotificationRepositoryImpl;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(LogRepositoryImpl.class)
public interface LogRepository {
    CompletionStage<Stream<Log>> getAllLogsOfAStage(long stageId);
    CompletionStage<Stream<Log>> getAllLogsOfAStageAndRider(long stageId, long riderId);
    CompletionStage<Stream<Log>> getAllLogsOfAStageAndRiderAndNotificationType(long stageId, long riderId, NotificationType type);
    CompletionStage<Log> addLog(Log log);
    CompletionStage<Stream<Log>> deleteAllLogsOfAStage(long stageId);
    CompletionStage<Stream<Log>> deleteAllLogs();
}
