package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Judgment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.cache.AsyncCacheApi;
import play.cache.Cached;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.JudgmentRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Api("Judgment")
public class JudgmentController extends Controller {
    private final JudgmentRepository judgmentRepository;
    private static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";
    private static final int CACHE_DURATION = 10;
    private final AsyncCacheApi cache;

    @Inject
    public JudgmentController(JudgmentRepository judgmentRepository, AsyncCacheApi cache) { this.judgmentRepository = judgmentRepository; this.cache = cache;}

    @ApiOperation(value ="get all judgments of a race", response = Judgment.class, responseContainer = "List")
    @Cached(key = "judgments", duration = CACHE_DURATION)
    public CompletionStage<Result> getJudgments() {
        return judgmentRepository.getAllJudgments().thenApplyAsync(judgments -> ok(toJson(judgments))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No judgments are set in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get all judgments of a stage", response = Judgment.class, responseContainer = "List")
    public CompletionStage<Result> getJudgmentsByStage(long stageId) {
        return cache.getOrElseUpdate("judgments/stages/"+stageId, () -> judgmentRepository.getJudgmentsByStage(stageId).thenApplyAsync(judgments -> ok(toJson(judgments))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No judgments are set in DB for the specific stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), CACHE_DURATION);
    }

    @ApiOperation(value ="get all judgments of a rider", response = Judgment.class, responseContainer = "List")
    public CompletionStage<Result> getJudgmentsByRider(long riderId) {
        return cache.getOrElseUpdate("judgments/riders/"+riderId, () -> judgmentRepository.getJudgmentsByRider(riderId).thenApplyAsync(judgments -> ok(toJson(judgments))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No judgments are set in DB for specific rider.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), CACHE_DURATION);
    }
}
