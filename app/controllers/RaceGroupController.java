package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.RaceGroup;
import models.Stage;
import models.enums.RaceGroupType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RaceGroupRepository;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class RaceGroupController extends Controller {
    private final RaceGroupRepository raceGroupRepository;
    private final StageRepository stageRepository;

    @Inject
    public RaceGroupController(RaceGroupRepository raceGroupRepository) {
        this.raceGroupRepository = raceGroupRepository;
        this.
    }

    public CompletionStage<Result> getAllRaceGroups(long stageid) {
        return raceGroupRepository.getAllRaceGroups(stageid).thenApplyAsync(raceGroups -> ok(toJson(raceGroups.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()) {
                case "IndexOutOfBoundsException":
                    res = badRequest("No racegroups are set in db");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    public CompletionStage<Result> getRaceGroup(long id) {
        return raceGroupRepository.getRaceGroupById(id).thenApplyAsync(raceGroup -> ok(toJson(raceGroup))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("No racegroup with id: " + id + " is available in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addRaceGroup() {
        JsonNode json = request().body().asJson();
        return parseNewRaceGroup(json).thenApply(raceGroupRepository::addRaceGroup).thenApply(raceGroup -> ok("success")).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NullPointerException":
                    res = badRequest("json format of racegroup was wrong");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateRaceGroup(long raceGroupId) {
        JsonNode json = request().body().asJson();
        return parseUpdateRaceGroup(json, raceGroupId).thenApply(raceGroupRepository::updateRaceGroup).thenApply(raceGroup -> ok("success")).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NullPointerException":
                    res = badRequest("json format of racegroup was wrong");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }


    private CompletableFuture<RaceGroup> parseNewRaceGroup (JsonNode json) {
        CompletableFuture<RaceGroup> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                RaceGroup raceGroup = new RaceGroup();
                raceGroup.setTimestamp(Timestamp.valueOf(json.findPath("timestamp").textValue()));
                String raceGroupType = json.findPath("type").textValue();
                raceGroup.setRaceGroupType(RaceGroupType.valueOf(raceGroupType));
                raceGroup.setHistoryGapTime(json.findPath("actualGapTime").longValue());
                raceGroup.setActualGapTime(json.findPath("actualGapTime").longValue());
                raceGroup.setPosition(json.findPath("actualGapTime").intValue());
                raceGroup.setRiders(null);
                final Stage[] st = new Stage[1];
                long stageId = json.findPath("stageId").longValue();
                stageRepository.getStage(stageId).thenApply(stage -> st[0] = stage).toCompletableFuture().join();
                raceGroup.setStage(st[0]);
                completableFuture.complete(raceGroup);
            } catch (Exception e) {
                completableFuture.obtrudeException(e);
                throw e;
            }
        });

        return completableFuture;
    }

    private CompletableFuture<RaceGroup> parseUpdateRaceGroup (JsonNode json, long raceGroupId) {
        CompletableFuture<RaceGroup> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                RaceGroup raceGroup = new RaceGroup();
                raceGroup.setId(raceGroupId);
                raceGroup.setTimestamp(Timestamp.valueOf(json.findPath("timestamp").textValue()));
                String raceGroupType = json.findPath("type").textValue();
                raceGroup.setRaceGroupType(RaceGroupType.valueOf(raceGroupType));
                raceGroup.setHistoryGapTime(json.findPath("actualGapTime").longValue());
                raceGroup.setActualGapTime(json.findPath("actualGapTime").longValue());
                raceGroup.setPosition(json.findPath("actualGapTime").intValue());
                raceGroup.setRiders(null);
                final Stage[] st = new Stage[1];
                long stageId = json.findPath("stageId").longValue();
                stageRepository.getStage(stageId).thenApply(stage -> st[0] = stage).toCompletableFuture().join();
                raceGroup.setStage(st[0]);
                completableFuture.complete(raceGroup);
            } catch (Exception e) {
                completableFuture.obtrudeException(e);
                throw e;
            }
        });

        return completableFuture;
    }
}
