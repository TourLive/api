package repository.interfaces;

import models.Notification;

import java.sql.Timestamp;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface NotificationRepository {
    CompletionStage<Stream<Notification>> getAllNotifications();
    void addNotifications(CompletionStage<Stream<Notification>> notifications);
    CompletionStage<Stream<Notification>> getNotificationsByTimestamp(Timestamp timestamp);
    void deleteAllNotification();
}
