package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.RiderRanking;
import models.enums.RankingType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RiderRankingRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("RiderRanking")
public class RiderRankingConnectionController extends Controller {
    private final RiderRankingRepository riderRankingRepository;
    private static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";

    @Inject
    public RiderRankingConnectionController(RiderRankingRepository riderRankingRepository) {
        this.riderRankingRepository = riderRankingRepository;
    }


    @ApiOperation(value ="get all riderrankings of a rider stage connection", response = RiderRanking.class)
    public CompletionStage<Result> getRiderRankings(long riderStageConnectionId) {
        return riderRankingRepository.getAllRiderRankings(riderStageConnectionId).thenApplyAsync(riderRankings -> ok(toJson(riderRankings.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No rider rankings are set in DB for this riderStageConnection Id.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get the riderrankings by ranking type and rider stage connection id", response = RiderRanking.class)
    public CompletionStage<Result> getRiderRankingsByType(long riderStageConnectionId, String rankingType) {
        return riderRankingRepository.getAllRiderRankingsByType(riderStageConnectionId, rankingType).thenApplyAsync(riderRankings -> ok(toJson(riderRankings.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No rider rankings are set in DB for this riderStageConnection id and Type.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get all riderrankings by ranking type and rider", response = RiderRanking.class)
    public CompletionStage<Result> getRiderRankingByRiderAndType(long riderId, String rankingType) {
        return riderRankingRepository.getRiderRankingByRiderAndType(riderId, rankingType).thenApplyAsync(riderRanking -> ok(toJson(riderRanking))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No rider rankings are set in DB for this rider id and type.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="update a rider ranking")
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateRiderRanking(long riderRankingId) {
        JsonNode json = request().body().asJson();
        return parseRiderRanking(json, riderRankingId).thenApply(riderRankingRepository::updateRiderRanking).thenApplyAsync(rSC -> ok("success")).exceptionally(ex -> {
            Result res = null;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("The specific riderRanking with the id was not found in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    private CompletableFuture<RiderRanking> parseRiderRanking(JsonNode json, Long riderRankingId){
        CompletableFuture<RiderRanking> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try{
                RiderRanking riderRanking = new RiderRanking();
                riderRanking.setId(riderRankingId);
                riderRanking.setRank(json.findPath("rank").asInt());
                riderRanking.setRankingType(RankingType.valueOf(json.findPath("rankingTyp").textValue()));
                completableFuture.complete(riderRanking);
                return riderRanking;
            } catch (Exception e){
                completableFuture.obtrudeException(e);
                throw  e;
            }
        });

        return completableFuture;
    }


}
