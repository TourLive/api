package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.RaceGroup;
import models.enums.RaceGroupType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RaceGroupRepository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class RaceGroupController extends Controller {
    private final RaceGroupRepository raceGroupRepository;

    @Inject
    public RaceGroupController(RaceGroupRepository raceGroupRepository) {
        this.raceGroupRepository = raceGroupRepository;
    }

    public CompletionStage<Result> getAllRaceGroups() {
        return raceGroupRepository.getAllRaceGroups().thenApplyAsync(raceGroups -> ok(toJson(raceGroups.collect(Collectors.toList())))).exceptionally(ex -> {
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

    public CompletionStage<Result> deleteAllRaceGroups() {
        return raceGroupRepository.deleteAllRaceGroups().thenApply(raceGroups -> ok(toJson(raceGroups.collect(Collectors.toList())) + "have been deleted")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    public CompletionStage<Result> deleteRaceGroup(long id) {
        return raceGroupRepository.deleteRaceGroupById(id).thenApplyAsync(racegroup -> ok(toJson(racegroup) + "has been deleted")).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()) {
                case "NoResultException":
                    res = badRequest("Racegroup with id" + id + " not found in DB");
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
        return parseRaceGroup(json).thenApply(raceGroupRepository::addRaceGroup).thenApply(raceGroup -> ok(toJson(raceGroup) + " has been added")).exceptionally(ex -> {
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

    private CompletableFuture<RaceGroup> parseRaceGroup (JsonNode json) {
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
            } catch (Exception e) {
                completableFuture.obtrudeException(e);
                throw e;
            }
        });

        return completableFuture;
    }
}
