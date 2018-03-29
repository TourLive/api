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
    CompletionStage<Stream<Race>> getAllRaces();
    CompletionStage<Race> getRace(Long raceId);
    void addRace(Race race);
    void deleteAllRaces();
    void deleteRace(Long id);
}
