package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.RaceGroup;
import models.Rider;
import models.enums.RaceGroupType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.cache.AsyncCacheApi;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
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
    private static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";
    private static final String NORESULTEXCEPTION = "NoResultException";
    private static final String ACTUAL_GAP_TIME = "ACTUAL_GAP_TIME";
    private static final String HISTORY_GAP_TIME = "HISTORY_GAP_TIME";
    private static final int CACHE_DURATION = 10;
    private final AsyncCacheApi cache;

    @Inject
    public RaceGroupController(RaceGroupRepository raceGroupRepository, StageRepository stageRepository, RiderRepository riderRepository, AsyncCacheApi cache) {
        this.raceGroupRepository = raceGroupRepository;
        this.stageRepository = stageRepository;
        this.riderRepository = riderRepository;
        this.cache = cache;
    }

    @ApiOperation(value ="get all racegroups of a stage", response = RaceGroup.class, responseContainer = "List")
    public CompletionStage<Result> getAllRaceGroups(long stageId) {
        return cache.getOrElseUpdate("racegroups/stages/"+stageId, () -> raceGroupRepository.getAllRaceGroups(stageId).thenApplyAsync(raceGroups -> ok(toJson(raceGroups.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No racegroups are set in DB for this stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), CACHE_DURATION);
    }

    @ApiOperation(value ="get a racegroup by id", response = RaceGroup.class)
    public CompletionStage<Result> getRaceGroup(long id) {
        return cache.getOrElseUpdate("racegroup/"+id, () -> raceGroupRepository.getRaceGroupById(id).thenApplyAsync(raceGroup -> ok(toJson(raceGroup))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(NORESULTEXCEPTION)){
                res = badRequest("No racegroup with id: " + id + " is available in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), CACHE_DURATION);
    }

    @ApiOperation(value ="manage racegroups", response = String.class)
    @BodyParser.Of(BodyParser.Json.class)
    @With(BasicAuthAction.class)
    public CompletionStage<Result> manageRaceGroups(long stageId) {
        List<RaceGroup> dbRaceGroups = raceGroupRepository.getAllRaceGroups(stageId).toCompletableFuture().join().collect(Collectors.toList());
        HashMap<String, RaceGroup> stringRaceGroupHashMap = new HashMap<>();
        for(RaceGroup raceGroup : dbRaceGroups){
            stringRaceGroupHashMap.put(raceGroup.getAppId(), raceGroup);
        }
        List<RaceGroup> receivedRaceGroups = parseRaceGroups(request().body().asJson(), stageId).toCompletableFuture().join();
        for(RaceGroup raceGroup : receivedRaceGroups){
            // Allready persisted raceGroup -> UpdateRaceGroup
            if(raceGroup.getRaceGroupType() == RaceGroupType.FELD || stringRaceGroupHashMap.containsKey(raceGroup.getAppId())){
                if(raceGroup.getRaceGroupType() != RaceGroupType.FELD){
                    raceGroup.setId(stringRaceGroupHashMap.get(raceGroup.getAppId()).getId());
                    dbRaceGroups.remove(stringRaceGroupHashMap.get(raceGroup.getAppId()));
                } else {
                    dbRaceGroups.remove(raceGroup);
                }
                raceGroupRepository.updateRaceGroup(raceGroup);
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
                    raceGroup.setHistoryGapTime(raceGroupJson.findPath(HISTORY_GAP_TIME).longValue());
                    raceGroup.setActualGapTime(raceGroupJson.findPath(ACTUAL_GAP_TIME).longValue());
                    raceGroup.setPosition(raceGroupJson.findPath("position").intValue());
                    raceGroup.setAppId(raceGroupJson.findPath("id").asText());
                    ArrayList<Rider> riders = new ArrayList<>();
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
            }
        });

        return completableFuture;
    }

    @ApiOperation(value ="update specific racegroups time", response = String.class)
    @BodyParser.Of(BodyParser.Json.class)
    @With(BasicAuthAction.class)
    public CompletionStage<Result> updateRaceGroup(String raceGroupId, long stageId) {
        RaceGroup raceGroup = null;
        try{
            raceGroup = raceGroupRepository.getRaceGroupByAppId(raceGroupId).toCompletableFuture().join();
        } catch (Exception ex){
            raceGroup = raceGroupRepository.getRaceGroupField(stageId);
            raceGroup.setAppId(raceGroupId);
        }
        raceGroup = parseRaceGroup(request().body().asJson(), raceGroup).toCompletableFuture().join();
        raceGroupRepository.updateRaceGroup(raceGroup);
        return CompletableFuture.completedFuture(ok());
    }

    private CompletableFuture<RaceGroup> parseRaceGroup (JsonNode json, RaceGroup raceGroup) {
        CompletableFuture<RaceGroup> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                raceGroup.setActualGapTime(json.findPath(ACTUAL_GAP_TIME).longValue());
                raceGroup.setHistoryGapTime(json.findPath(HISTORY_GAP_TIME).longValue());
                completableFuture.complete(raceGroup);
            } catch (Exception e) {
                completableFuture.obtrudeException(e);
                throw e;
            }
        });

        return completableFuture;
    }
}
