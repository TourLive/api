package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.RaceGroup;
import repository.RaceGroupRepositoryImpl;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RaceGroupRepositoryImpl.class)
public interface RaceGroupRepository {
    CompletionStage<Stream<RaceGroup>> getAllRaceGroups(long stageId);
    List<RaceGroup> getAllRaceGroupsSync(long stageId);
    CompletionStage<RaceGroup> getRaceGroupById(long id);
    CompletionStage<RaceGroup> getRaceGroupByAppId(String id);
    RaceGroup getRaceGroupField(long stageId);
    CompletionStage<RaceGroup> addRaceGroup(RaceGroup raceGroup, long timestamp);
    CompletionStage<RaceGroup> updateRaceGroup(RaceGroup raceGroup, long timestamp);
    void deleteAllRaceGroups();
    void deleteRaceGroupById(long id);
    CompletionStage<Stream<RaceGroup>> deleteAllRaceGroupsOfAStage(long stageId);
}
