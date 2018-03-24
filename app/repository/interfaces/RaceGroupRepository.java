package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.RaceGroup;
import repository.RaceGroupRepositoryImpl;

import java.sql.Timestamp;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RaceGroupRepositoryImpl.class)
public interface RaceGroupRepository {
    CompletionStage<Stream<RaceGroup>> getAllRaceGroups();
    CompletionStage<Stream<RaceGroup>> getRaceGroupsByTimestamp(Timestamp timestamp);
    void addRaceGroup(RaceGroup raceGroup);
    void updateRaceGroup(RaceGroup raceGroup);
    void deleteAllRaceGroups();
    void deleteRaceGroupByPosition(int position);
}
