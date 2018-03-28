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
        return raceRepository.getAllRaces().thenApplyAsync(races -> ok(toJson(races.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
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

    public CompletionStage<Result> getRace(Long raceId) {
        return raceRepository.getRace(raceId).thenApplyAsync(race -> ok(toJson(race))).exceptionally(ex -> {
            Result res;
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

    /*
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> setRace() {
        JsonNode json = request().body().asJson();
        Race race = new Race();
        race.setName(json.findPath("name").textValue());

        return raceRepository.addRace(race).thenApplyAsync(racePersisted -> ok(toJson(racePersisted) + " has been added")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    public CompletionStage<Result> deleteAllRaces() {
        return raceRepository.deleteAllRaces().thenApply(races -> ok(toJson(races.collect(Collectors.toList())) +  "have been deleted")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    public CompletionStage<Result> deleteRace (Long id) {
        return raceRepository.deleteRace(id).thenApplyAsync(race -> ok(toJson(race) + " has been deleted")).exceptionally(ex -> {
            Result res ;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("Race " + id + " not found in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }*/
}
