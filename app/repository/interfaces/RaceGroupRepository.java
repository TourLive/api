package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.RaceGroup;
import repository.RaceGroupRepositoryImpl;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RaceGroupRepositoryImpl.class)
public interface RaceGroupRepository {
    CompletionStage<Stream<RaceGroup>> getAllRaceGroups();
    CompletionStage<RaceGroup> getRaceGroupById(long id);
    CompletionStage<RaceGroup> addRaceGroup(RaceGroup raceGroup);
    CompletionStage<RaceGroup> updateRaceGroup(RaceGroup raceGroup);
    CompletionStage<Stream<RaceGroup>> deleteAllRaceGroups();
    CompletionStage<RaceGroup> deleteRaceGroupById(long id);
}
