package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Race;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RaceRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static play.libs.Json.toJson;

public class RaceController extends Controller {
    private final RaceRepository raceRepository;

    @Inject
    public RaceController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    public CompletionStage<Result> getAllRaces() {
        return raceRepository.getAllRaces().thenApplyAsync(races -> {
            return ok(races);
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No races are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    public CompletionStage<Result> getRace(int raceId) {
        return raceRepository.getRace(raceId).thenApplyAsync(race -> {
           return ok(race);
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("Race with id: " + raceId + " is not available in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> setRace() {
        JsonNode json = request().body().asJson();
        Race race = new Race();
        race.setName(json.findPath("name").textValue());
        race.setRaceId(json.findPath("raceId").intValue());

        return raceRepository.setRace(race).thenApplyAsync(racePersisted -> {
            return ok(racePersisted + " has been added");
        }).exceptionally(ex -> {return internalServerError(ex.getMessage());});
    }

    public CompletionStage<Result> deleteAllRaces() {
        return raceRepository.deleteAllRaces().thenApply(races -> {
            return ok(races +  "have been deleted");

        }).exceptionally(ex -> {return internalServerError(ex.getMessage());});
    }

    public CompletionStage<Result> deleteRace (String name) {
        return raceRepository.deleteRace(name).thenApplyAsync(race -> {
                return ok(toJson(race + " has been deleted"));
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("Race " + name + " not found in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
