package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Race;
import models.RiderStageConnection;
import models.Stage;
import models.enums.StageType;
import models.enums.TypeState;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RaceRepository;
import repository.interfaces.RiderRepository;
import repository.interfaces.RiderStageConnectionRepository;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;

import static play.libs.Json.toJson;

public class RiderStageConnectionController extends Controller {
    private final RiderStageConnectionRepository riderStageConnectionRepository;

    @Inject
    public RiderStageConnectionController(RiderStageConnectionRepository riderStageConnectionRepository) {
        this.riderStageConnectionRepository = riderStageConnectionRepository;
    }


    public CompletionStage<Result> getRiderStageConnections(long stageId) {
        return riderStageConnectionRepository.getAllRiderStageConnections(stageId).thenApplyAsync(riderStageConnections -> {
            return ok(toJson(riderStageConnections));
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

    private CompletableFuture<RiderStageConnection> parseRiderStageConnection(JsonNode json, Long riderStageConnectionId){
        CompletableFuture<RiderStageConnection> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try{
                RiderStageConnection riderStageConnection = new RiderStageConnection();
                riderStageConnection.setId(riderStageConnectionId);
                riderStageConnection.setBonusPoints(json.findPath("bonusPoints").asInt());
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
