package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Race;
import repository.RaceRepositoryImpl;

import java.util.concurrent.CompletionStage;

@ImplementedBy(RaceRepositoryImpl.class)
public interface RaceRepository {
    CompletionStage<Race> getRace();
    void setRace(Race race);
    void deleteAllRaces();
    void deleteRace(String name);
}
