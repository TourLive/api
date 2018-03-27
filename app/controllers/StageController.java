package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Race;
import models.Stage;
import models.enums.StageType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RaceRepository;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class StageController extends Controller {
    private final StageRepository stageRepository;
    private final RaceRepository raceRepository;

    @Inject
    public StageController(StageRepository stageRepository, RaceRepository raceRepository) {
        this.stageRepository = stageRepository;
        this.raceRepository = raceRepository;
    }

    public CompletionStage<Result> getStages() {
        return stageRepository.getAllStages().thenApplyAsync(stages -> ok(toJson(stages.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No stage are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }


    public CompletionStage<Result> getStage(long stageId) {
        return stageRepository.getStage(stageId).thenApplyAsync(stage -> ok(toJson(stage))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("No Stage with id: " + stageId + ", is available in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> addStage() {
        JsonNode json = request().body().asJson();
        return parseStage(json).thenApply(stageRepository::addStage).thenApply(message -> ok(toJson(message) + " has been added")).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NullPointerException":
                    res = badRequest("json format of stage was wrong");
                    break;
                case "NoResultException":
                    res = badRequest("race was not found");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    private CompletableFuture<Stage> parseStage(JsonNode json){
        CompletableFuture<Stage> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try{
            Stage stage = new Stage();
            stage.setStageType(StageType.valueOf(json.findPath("type").textValue()));
            stage.setDistance(json.findPath("distance").intValue());
            stage.setEndTime(new Date(json.findPath("endTime").longValue()));
            stage.setStartTime(new Date(json.findPath("startTime").longValue()));
            stage.setStart(json.findPath("start").textValue());
            stage.setDestination(json.findPath("destination").textValue());
            final Race[] r = new Race[1];
            long raceId = json.findPath("raceId").longValue();
            raceRepository.getRace(raceId).thenApply(race -> r[0] = race).toCompletableFuture().join();
            stage.setRace(r[0]);
            completableFuture.complete(stage);
            return stage;
            } catch (Exception e){
                completableFuture.obtrudeException(e);
                throw  e;
            }
        });

        return completableFuture;
    }

    public CompletionStage<Result> deleteAllStages() {
        return stageRepository.deleteAllStages().thenApply(stages -> ok(toJson(stages.collect(Collectors.toList())) +  " have been deleted")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    public CompletionStage<Result> deleteStage (long stageId) {
        return stageRepository.deleteStage(stageId).thenApplyAsync(stage -> ok(toJson(stage) + " has been deleted")).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("Stage with Id: " + stageId + " ,not found in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
