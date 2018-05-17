package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Race;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.cache.AsyncCacheApi;
import play.cache.Cached;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RaceRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("Race")
public class RaceController extends Controller {
    private final RaceRepository raceRepository;
    private final AsyncCacheApi cache;

    @Inject
    public RaceController(RaceRepository raceRepository, AsyncCacheApi cache) { this.raceRepository = raceRepository; this.cache = cache; }

    @ApiOperation(value ="get all races", response = Race.class, responseContainer = "List")
    @Cached(key="races", duration = GlobalConstants.CACHE_DURATION)
    public CompletionStage<Result> getAllRaces() {
        return raceRepository.getAllRaces().thenApplyAsync(races -> ok(toJson(races.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No races are set in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get race by id", response = Race.class)
    public CompletionStage<Result> getRace(Long raceId) {
        return cache.getOrElseUpdate("race/"+raceId, () -> raceRepository.getRace(raceId).thenApplyAsync(race -> ok(toJson(race))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(GlobalConstants.NORESULTEXCEPTION)){
                res = badRequest("Race with id: " + raceId + " is not available in DB.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        }), GlobalConstants.CACHE_DURATION);
    }
}
