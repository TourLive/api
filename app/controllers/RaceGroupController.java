package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.RaceGroup;
import models.Rider;
import models.Stage;
import models.enums.RaceGroupType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repository.interfaces.RaceGroupRepository;
import repository.interfaces.RiderRepository;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("Racegroup")
public class RaceGroupController extends Controller {
    private final RaceGroupRepository raceGroupRepository;
    private final StageRepository stageRepository;
    private final RiderRepository riderRepository;

    @Inject
    public RaceGroupController(RaceGroupRepository raceGroupRepository, StageRepository stageRepository, RiderRepository riderRepository) {
        this.raceGroupRepository = raceGroupRepository;
        this.stageRepository = stageRepository;
        this.riderRepository = riderRepository;
    }

    @ApiOperation(value ="get all racegroups of a stage", response = RaceGroup.class)
    public CompletionStage<Result> getAllRaceGroups(long stageId) {
        return raceGroupRepository.getAllRaceGroups(stageId).thenApplyAsync(raceGroups -> ok(toJson(raceGroups.collect(Collectors.toList())))).exceptionally(ex -> {
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

    @ApiOperation(value ="get a racegroup by id", response = RaceGroup.class)
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

    @ApiOperation(value ="manage racegroups")
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> manageRaceGroups(long stageId) {
        List<RaceGroup> dbRaceGroups = raceGroupRepository.getAllRaceGroups(stageId).toCompletableFuture().join().collect(Collectors.toList());
        HashMap<String, RaceGroup> stringRaceGroupHashMap = new HashMap<>();
        for(RaceGroup raceGroup : dbRaceGroups){
            stringRaceGroupHashMap.put(raceGroup.getAppId(), raceGroup);
        }
        Http.Request r = request();
        List<RaceGroup> receivedRaceGroups = parseRaceGroups(r.body().asJson(), stageId).toCompletableFuture().join();
        for(RaceGroup raceGroup : receivedRaceGroups){
            // Allready persisted raceGroup -> UpdateRaceGroup
            if(raceGroup.getRaceGroupType() == RaceGroupType.FELD || stringRaceGroupHashMap.containsKey(raceGroup.getAppId())){
                raceGroup.setId(stringRaceGroupHashMap.get(raceGroup.getAppId()).getId());
                raceGroupRepository.updateRaceGroup(raceGroup);
                dbRaceGroups.remove(stringRaceGroupHashMap.get(raceGroup.getAppId()));
            } else {
                // New RaceGroup
                raceGroupRepository.addRaceGroup(raceGroup);
            }
        }
        // Delete old RaceGroups
        for(RaceGroup raceGroup : dbRaceGroups){
            raceGroupRepository.deleteRaceGroupById(raceGroup.getId());
        }
        return CompletableFuture.completedFuture(ok());
    }

    private CompletableFuture<List<RaceGroup>> parseRaceGroups (JsonNode json, long stageId) {
        CompletableFuture<List<RaceGroup>> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            ArrayList<RaceGroup> raceGroups = new ArrayList<>();
            List<Rider> allRiders = riderRepository.getAllRiders(stageId).toCompletableFuture().join().collect(Collectors.toList());
            HashMap<Long, Rider> riderHashMap = new HashMap<>();
            for(Rider r : allRiders){
                riderHashMap.put(r.getId(), r);
            }
            try {
                ArrayNode node = (ArrayNode) new ObjectMapper().readTree(json.asText());
                for(JsonNode raceGroupJson : node) {
                    RaceGroup raceGroup = new RaceGroup();
                    String raceGroupType = raceGroupJson.findPath("type").textValue();
                    raceGroup.setRaceGroupType(RaceGroupType.valueOf(raceGroupType));
                    raceGroup.setHistoryGapTime(raceGroupJson.findPath("actualGapTime").longValue());
                    raceGroup.setActualGapTime(raceGroupJson.findPath("actualGapTime").longValue());
                    raceGroup.setPosition(raceGroupJson.findPath("position").intValue());
                    raceGroup.setAppId(raceGroupJson.findPath("id").asText());
                    ArrayList<Rider> riders = new ArrayList<Rider>();
                    for (JsonNode rider : (ArrayNode) raceGroupJson.findPath("riders")) {
                        riders.add(riderHashMap.get(rider.longValue()));
                    }
                    raceGroup.setRiders(riders);
                    raceGroup.setStage(stageRepository.getStage(stageId).toCompletableFuture().join());
                    raceGroups.add(raceGroup);
                }
                completableFuture.complete(raceGroups);

            } catch (Exception e) {
                completableFuture.obtrudeException(e);
                //throw e;
            }
        });

        return completableFuture;
    }



    @ApiOperation(value ="add new racegroup")
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

    @ApiOperation(value ="update a specific racegroup")
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
                String raceGroupType = json.findPath("type").textValue();
                raceGroup.setRaceGroupType(RaceGroupType.valueOf(raceGroupType));
                raceGroup.setHistoryGapTime(json.findPath("actualGapTime").longValue());
                raceGroup.setActualGapTime(json.findPath("actualGapTime").longValue());
                raceGroup.setPosition(json.findPath("actualGapTime").intValue());
                raceGroup.setAppId(json.findPath("appId").textValue());
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
