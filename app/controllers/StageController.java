package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Stage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("Stage")
public class StageController extends Controller {
    private final StageRepository stageRepository;
    private static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";
    private static final String NORESULTEXCEPTION = "NoResultException";

    @Inject
    public StageController(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @ApiOperation(value ="get all stages", response = Stage.class)
    public CompletionStage<Result> getStages() {
        return stageRepository.getAllStages().thenApplyAsync(stages -> ok(toJson(stages.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No stages are set in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }


    @ApiOperation(value ="get stage by id", response = Stage.class)
    public CompletionStage<Result> getStage(long stageId) {
        return stageRepository.getStage(stageId).thenApplyAsync(stage -> ok(toJson(stage))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(NORESULTEXCEPTION)){
                res = badRequest("No Stage with id: " + stageId + ", is available in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
