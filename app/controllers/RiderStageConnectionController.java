package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.importutilities.comparators.StartNrComparator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.RiderStageConnection;
import models.enums.TypeState;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
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

    @Inject
    public RiderStageConnectionController(RiderStageConnectionRepository riderStageConnectionRepository) {
        this.riderStageConnectionRepository = riderStageConnectionRepository;
    }


    @ApiOperation(value ="get all rider stage connections of a stage", response = RiderStageConnection.class)
    public CompletionStage<Result> getRiderStageConnections(long stageId) {
        return riderStageConnectionRepository.getAllRiderStageConnections(stageId).thenApplyAsync(riderStageConnections -> {
            List<RiderStageConnection> returnValues = riderStageConnections.collect(Collectors.toList());
            returnValues.sort(new StartNrComparator());
            return ok(toJson(returnValues));
        }).exceptionally(ex -> {
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

    @ApiOperation(value ="get the rider stage connection of a rider in a specific stage", response = RiderStageConnection.class)
    public CompletionStage<Result> getRiderStageConnection(long stageId, long riderId) {
        return riderStageConnectionRepository.getRiderStageConnectionByRiderAndStage(stageId, riderId).thenApplyAsync(riderStageConnection -> {
            return ok(toJson(riderStageConnection));
        }).exceptionally(ex -> {
            Result res = null;
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

    @ApiOperation(value = "update a existing rider stage connection")
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateRiderStageConnection(long riderStageConnectionId) {
        JsonNode json = request().body().asJson();
        return parseRiderStageConnection(json, riderStageConnectionId).thenApply(riderStageConnectionRepository::updateRiderStageConnection).thenApplyAsync(rSC -> {
            return ok("success");
        }).exceptionally(ex -> {
            Result res = null;
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
