package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Rider;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.cache.AsyncCacheApi;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RiderRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("Rider")
public class RiderController extends Controller {
    private final RiderRepository riderRepository;
    private final AsyncCacheApi cache;

    @Inject
    public RiderController(RiderRepository riderRepository, AsyncCacheApi cache) { this.riderRepository = riderRepository; this.cache = cache; }

    @ApiOperation(value ="get riders of a specific stage", response = Rider.class, responseContainer = "List")
    public CompletionStage<Result> getRiders(long stageId) {
        return cache.getOrElseUpdate("riders/stages/"+stageId, () -> riderRepository.getAllRiders(stageId).thenApplyAsync(riders -> ok(toJson(riders.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No riders are set in DB for this stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), GlobalConstants.LONG_CACHE_DURATION);
    }

    @ApiOperation(value ="get rider by id", response = Rider.class)
    public CompletionStage<Result> getRider(long riderId){
        return cache.getOrElseUpdate("rider/"+riderId, () -> riderRepository.getRiderAsync(riderId).thenApplyAsync(rider -> ok(toJson(rider))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.NORESULTEXCEPTION)){
                res = badRequest("No rider with id: " + riderId + " is available in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), GlobalConstants.LONG_CACHE_DURATION);
    }
}
