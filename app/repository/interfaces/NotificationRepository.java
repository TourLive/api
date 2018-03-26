package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Notification;
import repository.NotificationRepositoryImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(NotificationRepositoryImpl.class)
public interface NotificationRepository {
    CompletionStage<Stream<Notification>> getAllNotifications();
    CompletionStage<Stream<Notification>> getNotificationsByTimestamp(Timestamp timestamp);
    void addNotifications(ArrayList<Notification> notifications);
    void deleteAllNotification();
}
