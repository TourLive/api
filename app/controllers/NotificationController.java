package controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.NotificationRepository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.concurrent.CompletionStage;

public class NotificationController extends Controller {
    private final NotificationRepository notificationRepository;

    @Inject
    public NotificationController(NotificationRepository notificationRepository) { this.notificationRepository = notificationRepository; }

    public CompletionStage<Result> getNotifications() {
        return notificationRepository.getAllNotifications().thenApplyAsync(notifications -> {
            return ok(notifications);
        }).exceptionally(ex -> {
            Result res = null;
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
        return notificationRepository.getNotificationsByTimestamp(new Timestamp(timestamp)).thenApplyAsync(notifications -> {
            return ok(notifications);
        }).exceptionally(ex -> {
            Result res = null;
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
        return notificationRepository.deleteAllNotification().thenApply(notifications -> {
            return ok(notifications +  " have been deleted");
        }).exceptionally(ex -> {return internalServerError(ex.getMessage());});
    }
}
