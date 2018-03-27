package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.Notification;
import repository.NotificationRepositoryImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(NotificationRepositoryImpl.class)
public interface NotificationRepository {
    CompletionStage<JsonNode>  getAllNotifications();
    CompletionStage<JsonNode>  getNotificationsByTimestamp(Timestamp timestamp);
    CompletionStage<JsonNode>  addNotification(Notification notifications);
    CompletionStage<JsonNode>  deleteAllNotification();
}
