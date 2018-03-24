package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Race;
import repository.RaceRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RaceRepositoryImpl.class)
public interface RaceRepository {
    CompletionStage<Race> getRace();
    CompletionStage<Race> setRace(Race race);
    CompletionStage<Stream<Race>> deleteAllRaces();
    CompletionStage<Race> deleteRace(String name);
}
