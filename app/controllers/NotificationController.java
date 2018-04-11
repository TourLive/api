package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Notification;
import models.enums.NotificationType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.NotificationRepository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("Notification")
public class NotificationController extends Controller {
    private final NotificationRepository notificationRepository;
    private static final String indexOutOfBoundsException = "IndexOutOfBoundsException";
    private static final String nullPointerException = "NullPointerException";

    @Inject
    public NotificationController(NotificationRepository notificationRepository) { this.notificationRepository = notificationRepository; }

    @ApiOperation(value ="get notifications of a specific stage", response = Notification.class)
    public CompletionStage<Result> getNotifications(long stageId) {
        return notificationRepository.getAllNotifications(stageId).thenApplyAsync(notifications -> ok(toJson(notifications.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(indexOutOfBoundsException)){
                res = badRequest("No notifications are set in DB for this stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get notifications of a specific stage at a timestamp", response = Notification.class)
    public CompletionStage<Result> getNotificationsByStageAndTimestamp(Long stageId, Long timestamp) {
        return notificationRepository.getNotificationsByTimestamp(stageId, new Timestamp(timestamp)).thenApplyAsync(notifications -> ok(toJson(notifications.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(indexOutOfBoundsException)){
                res = badRequest("No notifications are set in DB for this stage with this specific timestamp.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="add a notification", response = Notification.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addNotification(Long stageId) {
        JsonNode json = request().body().asJson();
        return parseNotification(json).thenApply(notification -> notificationRepository.addNotification(stageId, notification)).
                thenApplyAsync(dbNotification -> ok(toJson(dbNotification)))
                .exceptionally(ex -> {
                    Result res;
                    if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(nullPointerException)){
                        res = badRequest("adding notification failed.");
                    } else {
                        res = internalServerError(ex.getMessage());
                    }
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

    private CompletableFuture<Notification> parseNotification(JsonNode json){
        CompletableFuture<Notification> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try{
                Notification notification = new Notification();
                notification.setMessage(json.findPath("message").textValue());
                notification.setNotificationType(NotificationType.valueOf(json.findPath("typeState").asText()));
                notification.setTimestamp(new Timestamp(json.findPath("timestampe").asLong()));
                completableFuture.complete(notification);
                return notification;
            } catch (Exception e){
                completableFuture.obtrudeException(e);
                throw  e;
            }
        });

        return completableFuture;
    }
}
