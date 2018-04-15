package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.JudgmentRiderConnection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.JudgmentRepository;
import repository.interfaces.JudgmentRiderConnectionRepository;
import repository.interfaces.RiderRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("JudgmentRiderConnection")
public class JudgmentRiderConnectionController extends Controller {
    private final JudgmentRiderConnectionRepository judgmentRiderConnectionRepository;
    private final RiderRepository riderRepository;
    private final JudgmentRepository judgmentRepository;
    private static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";
    private static final String NULLPOINTEREXCEPTION = "NullPointerException";

    @Inject
    public JudgmentRiderConnectionController(JudgmentRiderConnectionRepository judgmentRiderConnectionRepository, RiderRepository riderRepository, JudgmentRepository judgmentRepository) {
        this.judgmentRiderConnectionRepository = judgmentRiderConnectionRepository;
        this.riderRepository = riderRepository;
        this.judgmentRepository = judgmentRepository;
    }

    @ApiOperation(value ="get all judgment rider connections of a specific rider", response = JudgmentRiderConnection.class)
    public CompletionStage<Result> getJudgmentRiderConnection(long riderId) {
        return judgmentRiderConnectionRepository.getJudgmentRiderConnectionsByRider(riderId).thenApplyAsync(judgmentRiderConnection -> ok(toJson(judgmentRiderConnection.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No judgmentRiderConnection are set in DB for specific rider.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get all judgment rider connections of a specific stage", response = JudgmentRiderConnection.class)
    public CompletionStage<Result> getJudgmentRiderConnectionByStage(long stageId) {
        return judgmentRiderConnectionRepository.getJudgmentRiderConnectionsByStage(stageId).thenApplyAsync(judgmentRiderConnection -> ok(toJson(judgmentRiderConnection.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No judgmentRiderConnection are set in DB for specific rider.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="add new judgment rider connection")
    @BodyParser.Of(BodyParser.Json.class)
    @With(BasicAuthAction.class)
    public CompletionStage<Result> addJudgmentRiderConnection() {
        JsonNode json = request().body().asJson();
        return parseJudgmentRiderConnection(json).thenApply(judgmentRiderConnectionRepository::addJudgmentRiderConnection).thenApply(judgmentRiderConnection -> ok("success")).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(NULLPOINTEREXCEPTION)){
                res = badRequest("adding judgment rider connection failed.");
            } else {
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
                judgmentRiderConnection.setAppId(json.findPath("id").textValue());
                judgmentRiderConnection.setRank(json.findPath("rank").intValue());
                long riderId = json.findPath("riderId").longValue();
                judgmentRiderConnection.setRider(riderRepository.getRider(riderId));
                long judgmentId = json.findPath("judgementId").longValue();
                judgmentRiderConnection.setJudgment(judgmentRepository.getJudgmentById(judgmentId));
                completableFuture.complete(judgmentRiderConnection);
            } catch (Exception e) {
                completableFuture.obtrudeException(e);
                throw e;
            }
        });

        return completableFuture;
    }

    @ApiOperation(value ="delete a judgment rider connection by appId")
    public CompletionStage<Result> deleteJudgmentRiderConnection(String appId) {
        return judgmentRiderConnectionRepository.deleteJudgmentRiderConnection(appId).thenApply(judgmentRiderConnection -> ok("success")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }
}
