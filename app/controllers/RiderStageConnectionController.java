package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.importutilities.comparators.StartNrComparator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.RiderStageConnection;
import models.enums.TypeState;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.cache.AsyncCacheApi;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.RiderStageConnectionRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("RiderStageConnection")
public class RiderStageConnectionController extends Controller {
    private final RiderStageConnectionRepository riderStageConnectionRepository;
    private static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";
    private static final String NULLPOINTEREXCEPTION = "NullPointerException";
    private static final int CACHE_DURATION = 10;
    private final AsyncCacheApi cache;

    @Inject
    public RiderStageConnectionController(RiderStageConnectionRepository riderStageConnectionRepository, AsyncCacheApi cache) {
        this.riderStageConnectionRepository = riderStageConnectionRepository;
        this.cache = cache;
    }


    @ApiOperation(value ="get all rider stage connections of a stage", response = RiderStageConnection.class, responseContainer = "List")
    public CompletionStage<Result> getRiderStageConnections(long stageId) {
        return cache.getOrElseUpdate("riderstageconnections/"+stageId, () -> riderStageConnectionRepository.getAllRiderStageConnections(stageId).thenApplyAsync(riderStageConnections -> {
            List<RiderStageConnection> returnValues = riderStageConnections.collect(Collectors.toList());
            returnValues.sort(new StartNrComparator());
            return ok(toJson(returnValues));
        }).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No riderStageConnections are set in DB for this stage id.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), CACHE_DURATION);
    }

    @ApiOperation(value ="get the rider stage connection of a rider in a specific stage", response = RiderStageConnection.class)
    public CompletionStage<Result> getRiderStageConnection(long stageId, long riderId) {
        return cache.getOrElseUpdate("riderstageconnection/stage/"+stageId + "/rider" + riderId, () -> riderStageConnectionRepository.getRiderStageConnectionByRiderAndStage(stageId, riderId).thenApplyAsync(riderStageConnection -> ok(toJson(riderStageConnection))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(NULLPOINTEREXCEPTION)){
                res = badRequest("No riderStageConnections are set in DB for this stage id and rider id.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), CACHE_DURATION);
    }

    @ApiOperation(value = "update a existing rider stage connection", response = String.class)
    @BodyParser.Of(BodyParser.Json.class)
    @With(BasicAuthAction.class)
    public CompletionStage<Result> updateRiderStageConnection(long riderStageConnectionId) {
        JsonNode json = request().body().asJson();
        return parseRiderStageConnection(json, riderStageConnectionId).thenApply(riderStageConnectionRepository::updateRiderStageConnection).thenApplyAsync(rSC -> ok("success")).exceptionally(ex -> {
            Result res = null;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(NULLPOINTEREXCEPTION)){
                res = badRequest("Update of riderStageConnection failed, because it was not found in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    private CompletableFuture<RiderStageConnection> parseRiderStageConnection(JsonNode json, long riderStageConnectionId){
        CompletableFuture<RiderStageConnection> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try{
                RiderStageConnection riderStageConnection = new RiderStageConnection();
                riderStageConnection.setId(riderStageConnectionId);
                riderStageConnection.setBonusPoints(json.findPath("bonusPoint").asInt());
                riderStageConnection.setMountainBonusPoints(json.findPath("mountainBonusPoints").asInt());
                riderStageConnection.setSprintBonusPoints(json.findPath("sprintBonusPoints").asInt());
                riderStageConnection.setBonusTime(json.findPath("bonusTime").asInt());
                riderStageConnection.setMoney(json.findPath("money").asInt());
                riderStageConnection.setOfficialTime(json.findPath("officialTime").asLong());
                riderStageConnection.setOfficialGap(json.findPath("officialGap").asLong());
                riderStageConnection.setVirtualGap(json.findPath("virtualGap").asLong());
                riderStageConnection.setTypeState(TypeState.valueOf(json.findPath("typeState").asText()));
                completableFuture.complete(riderStageConnection);
                return riderStageConnection;
            } catch (Exception e){
                completableFuture.obtrudeException(e);
                throw  e;
            }
        });

        return completableFuture;
    }
}
