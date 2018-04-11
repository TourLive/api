package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Reward;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RewardRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Api("Reward")
public class RewardController extends Controller {
    private final RewardRepository rewardRepository;
    private static final String indexOutOfBoundsException = "IndexOutOfBoundsException";

    @Inject
    public RewardController(RewardRepository rewardRepository) { this.rewardRepository = rewardRepository; }

    @ApiOperation(value ="get all rewards", response = Reward.class)
    public CompletionStage<Result> getRewards() {
        return rewardRepository.getAllRewards().thenApplyAsync(rewards -> ok(toJson(rewards))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(indexOutOfBoundsException)){
                res = badRequest("No rewards are set in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
