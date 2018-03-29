package controllers;

import com.fasterxml.jackson.databind.JsonNode;
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

import static play.libs.Json.toJson;

public class RiderRankingConnectionController extends Controller {
    private final RiderRankingRepository riderRankingRepository;

    @Inject
    public RiderRankingConnectionController(RiderRankingRepository riderRankingRepository) {
        this.riderRankingRepository = riderRankingRepository;
    }


    public CompletionStage<Result> getRiderRankings(long riderStageConnectionId) {
        return riderRankingRepository.getAllRiderRankings(riderStageConnectionId).thenApplyAsync(riderRankings -> {
            return ok(toJson(riderRankings));
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No rider rankings are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    public CompletionStage<Result> getRiderRankingsByType(long riderStageConnectionId, String rankingType) {
        return riderRankingRepository.getAllRiderRankingsByType(riderStageConnectionId, rankingType).thenApplyAsync(riderRankings -> {
            return ok(toJson(riderRankings));
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No rider rankings are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    public CompletionStage<Result> getRiderRankingByRiderAndType(long riderId, String rankingType) {
        return riderRankingRepository.getRiderRankingByRiderAndType(riderId, rankingType).thenApplyAsync(riderRanking -> {
            return ok(toJson(riderRanking));
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No rider ranking is set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateRiderRanking(long riderRankingId) {
        JsonNode json = request().body().asJson();
        return parseRiderRanking(json, riderRankingId).thenApply(riderRankingRepository::updateRiderRanking).thenApplyAsync(rSC -> {
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

    private CompletableFuture<RiderRanking> parseRiderRanking(JsonNode json, Long riderRankingId){
        CompletableFuture<RiderRanking> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try{
                RiderRanking riderRanking = new RiderRanking();
                riderRanking.setId(riderRankingId);
                riderRanking.setRank(json.findPath("rank").asInt());
                riderRanking.setRankingType(RankingType.valueOf(json.findPath("rankingTyp").textValue()));;
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
