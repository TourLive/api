package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Judgment;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.JudgmentRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Api("Judgment")
public class JudgmentController extends Controller {
    private final JudgmentRepository judgmentRepository;
    private static final String indexOutOfBoundsException = "IndexOutOfBoundsException";

    @Inject
    public JudgmentController(JudgmentRepository judgmentRepository) { this.judgmentRepository = judgmentRepository; }

    @ApiOperation(value ="get all judgments of a race", response = Judgment.class)
    public CompletionStage<Result> getJudgments() {
        return judgmentRepository.getAllJudgments().thenApplyAsync(judgments -> ok(toJson(judgments))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(indexOutOfBoundsException)){
                res = badRequest("No judgments are set in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get all judgments of a stage", response = Judgment.class)
    public CompletionStage<Result> getJudgmentsByStage(long stageId) {
        return judgmentRepository.getJudgmentsByStage(stageId).thenApplyAsync(judgments -> ok(toJson(judgments))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(indexOutOfBoundsException)){
                res = badRequest("No judgments are set in DB for the specific stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get all judgments of a rider", response = Judgment.class)
    public CompletionStage<Result> getJudgmentsByRider(long riderId) {
        return judgmentRepository.getJudgmentsByRider(riderId).thenApplyAsync(judgments -> ok(toJson(judgments))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(indexOutOfBoundsException)){
                res = badRequest("No judgments are set in DB for specific rider.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
