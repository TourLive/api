package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.Race;
import play.libs.Json;
import repository.RaceRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RaceRepositoryImpl.class)
public interface RaceRepository {
    CompletionStage<JsonNode> getAllRaces();
    CompletionStage<JsonNode> getRace(int raceId);
    CompletionStage<Race> getDbRace(int raceId);
    CompletionStage<JsonNode> setRace(Race race);
    CompletionStage<JsonNode> deleteAllRaces();
    CompletionStage<JsonNode> deleteRace(String name);
}
