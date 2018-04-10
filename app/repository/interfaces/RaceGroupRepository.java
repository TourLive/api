package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.RaceGroup;
import repository.RaceGroupRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RaceGroupRepositoryImpl.class)
public interface RaceGroupRepository {
    CompletionStage<Stream<RaceGroup>> getAllRaceGroups(long stageId);
    CompletionStage<RaceGroup> getRaceGroupById(long id);
    CompletionStage<RaceGroup> getRaceGroupByAppId(String id);
    RaceGroup getRaceGroupField();
    CompletionStage<RaceGroup> addRaceGroup(RaceGroup raceGroup);
    CompletionStage<RaceGroup> updateRaceGroup(RaceGroup raceGroup);
    void deleteAllRaceGroups();
    void deleteRaceGroupById(long id);
}
