package controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.NotificationRepository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class NotificationController extends Controller {
    private final NotificationRepository notificationRepository;

    @Inject
    public NotificationController(NotificationRepository notificationRepository) { this.notificationRepository = notificationRepository; }

    public CompletionStage<Result> getNotifications() {
        return notificationRepository.getAllNotifications().thenApplyAsync(notifications -> ok(toJson(notifications.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No notifications are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    public CompletionStage<Result> getNotificationsByTimestamp(Long timestamp) {
        return notificationRepository.getNotificationsByTimestamp(new Timestamp(timestamp)).thenApplyAsync(notifications -> ok(toJson(notifications.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No notifications are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }


    public CompletionStage<Result> deleteAllNotifications() {
        return notificationRepository.deleteAllNotification().thenApply(notifications -> ok(toJson(notifications.collect(Collectors.toList())) +  " have been deleted")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }
}
