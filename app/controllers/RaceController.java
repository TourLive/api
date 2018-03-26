package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Race;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RaceRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static play.libs.Json.toJson;

public class RaceController extends Controller {
    private final RaceRepository raceRepository;

    @Inject
    public RaceController(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    public Result index() {
        return ok("Race REPO");
    }

    public CompletionStage<Result> getRace() {
        return raceRepository.getRace().thenApplyAsync(race -> {
           return ok(toJson("The actual race is: " + race.name));
        }).exceptionally(ex -> {return internalServerError(ex.getMessage());});
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> setRace() {
        JsonNode json = request().body().asJson();
        Race race = new Race();
        race.setName(json.findPath("name").textValue());

        CompletableFuture<Stream<Race>> delAllRaces = raceRepository.deleteAllRaces().toCompletableFuture();
        //delAllRaces.exceptionally(ex -> {return throwException(ex.getMessage());});
        delAllRaces.join();

        return raceRepository.setRace(race).thenApplyAsync(racePersisted -> {
            return ok(racePersisted.name + " has been added");
        }).exceptionally(ex -> {return internalServerError(ex.getMessage());});
    }

    public CompletionStage<Result> deleteAllRaces() {
        return raceRepository.deleteAllRaces().thenApply(raceStream -> {
            return ok(toJson(raceStream.collect(Collectors.toList())+  " have been deleted"));
        }).exceptionally(ex -> {return internalServerError(ex.getMessage());});
    }

    public CompletionStage<Result> deleteRace (String name) {
        return raceRepository.deleteRace(name).thenApplyAsync(raceStream -> {
                return ok(toJson(raceStream.name + " has been deleted"));
        }).exceptionally(ex -> {return internalServerError("race not found: " + ex.getMessage());});
    }
}
