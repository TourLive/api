package repository.interfaces;

import models.Race;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface RaceRepository {
    CompletionStage<Race> getRace();
    void setRace(CompletionStage<Race> race);
    void deleteAllRaces();
    void deleteStage(String name);
}
