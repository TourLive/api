package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Log;
import models.enums.NotificationType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.cache.AsyncCacheApi;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.LogRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("Log")
public class LogController extends Controller {
    private final LogRepository logRepository;
    private final AsyncCacheApi cache;

    @Inject
    public LogController(LogRepository logRepository, AsyncCacheApi cache) { this.logRepository = logRepository; this.cache = cache; }

    @ApiOperation(value ="get logs of a specific stage", response = Log.class, responseContainer = "List")
    public CompletionStage<Result> getLogsOfAStage(long stageId) {
        return cache.getOrElseUpdate("logs/"+stageId, () -> logRepository.getAllLogsOfAStage(stageId).thenApplyAsync(logs -> ok(toJson(logs.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No logs are set in DB for this stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), GlobalConstants.CACHE_DURATION);
    }

    @ApiOperation(value ="get logs of a specific stage and rider", response = Log.class, responseContainer = "List")
    public CompletionStage<Result> getLogsOfAStageAndRider(long stageId, long riderId) {
        return cache.getOrElseUpdate("logs/"+stageId +"/rider" + riderId, () -> logRepository.getAllLogsOfAStageAndRider(stageId, riderId).thenApplyAsync(logs -> ok(toJson(logs.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No logs are set in DB for this stage and rider.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), GlobalConstants.CACHE_DURATION);
    }

    @ApiOperation(value ="get logs of a specific stage and rider", response = Log.class, responseContainer = "List")
    public CompletionStage<Result> getLogsOfAStageAndRiderAndNotificationType(long stageId, long riderId, String type) {
        return cache.getOrElseUpdate("logs/"+stageId +"/rider" + riderId + "/type" + type, () -> logRepository.getAllLogsOfAStageAndRiderAndNotificationType(stageId, riderId, NotificationType.valueOf(type)).thenApplyAsync(logs -> ok(toJson(logs.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No logs are set in DB for this stage and rider with this type.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), GlobalConstants.CACHE_DURATION);
    }

    @ApiOperation(value ="delete logs of a specific stage", response = Log.class, responseContainer = "List")
    @With(BasicAuthAction.class)
    public CompletionStage<Result> deleteLogsOfAStage(long stageId) {
        return logRepository.deleteAllLogsOfAStage(stageId).thenApplyAsync(logs -> ok(toJson(logs.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No logs are set in DB for this stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="delete logs of a specific stage", response = Log.class, responseContainer = "List")
    @With(BasicAuthAction.class)
    public CompletionStage<Result> deleteAllLogs() {
        return logRepository.deleteAllLogs().thenApplyAsync(logs -> ok(toJson(logs.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No logs are set in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
