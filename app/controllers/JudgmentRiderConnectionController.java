package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import models.JudgmentRiderConnection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.JudgmentRepository;
import repository.interfaces.JudgmentRiderConnectionRepository;
import repository.interfaces.RiderRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;

import static play.libs.Json.toJson;

public class JudgmentRiderConnectionController extends Controller {
    private final JudgmentRiderConnectionRepository judgmentRiderConnectionRepository;
    private final RiderRepository riderRepository;
    private final JudgmentRepository judgmentRepository;

    @Inject
    public JudgmentRiderConnectionController(JudgmentRiderConnectionRepository judgmentRiderConnectionRepository, RiderRepository riderRepository, JudgmentRepository judgmentRepository) {
        this.judgmentRiderConnectionRepository = judgmentRiderConnectionRepository;
        this.riderRepository = riderRepository;
        this.judgmentRepository = judgmentRepository;
    }

    public CompletionStage<Result> getJudgmentRiderConnection(long riderId) {
        return judgmentRiderConnectionRepository.getJudgmentRiderConnectionsByRider(riderId).thenApplyAsync(judgmentRiderConnection -> ok(toJson(judgmentRiderConnection))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No judgmentRiderConnection are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addJudgmentRiderConnection() {
        JsonNode json = request().body().asJson();
        return parseJudgmentRiderConnection(json).thenApply(judgmentRiderConnectionRepository::addJudgmentRiderConnection).thenApply(judgmentRiderConnection -> ok("success")).exceptionally(ex -> {
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

    private CompletableFuture<JudgmentRiderConnection> parseJudgmentRiderConnection (JsonNode json) {
        CompletableFuture<JudgmentRiderConnection> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                JudgmentRiderConnection judgmentRiderConnection = new JudgmentRiderConnection();
                judgmentRiderConnection.setRank(json.findPath("rank").intValue());
                long riderId = json.findPath("riderId").longValue();
                judgmentRiderConnection.setRider(riderRepository.getRider(riderId));
                long judgmentId = json.findPath("judgmentId").longValue();
                judgmentRiderConnection.setJudgment(judgmentRepository.getJudgmentById(judgmentId));
                completableFuture.complete(judgmentRiderConnection);
            } catch (Exception e) {
                completableFuture.obtrudeException(e);
                throw e;
            }
        });

        return completableFuture;
    }
}
