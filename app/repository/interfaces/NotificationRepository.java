package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Notification;
import repository.NotificationRepositoryImpl;

import java.sql.Timestamp;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(NotificationRepositoryImpl.class)
public interface NotificationRepository {
    CompletionStage<Stream<Notification>> getAllNotifications(long stageId);
    CompletionStage<Stream<Notification>> getNotificationsByTimestamp(long stageId, Timestamp timestamp);
    CompletionStage<Notification> addNotification(long stageId, Notification notification);
    void deleteAllNotification();
}
