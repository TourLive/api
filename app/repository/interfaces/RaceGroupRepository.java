package repository.interfaces;

import models.Maillot;
import models.RaceGroup;

import java.sql.Timestamp;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface RaceGroupRepository {
    CompletionStage<Stream<RaceGroup>> getAllRaceGroups();
    CompletionStage<Stream<RaceGroup>> getRaceGroupsByTimestamp(Timestamp timestamp);
    void addRaceGroup(CompletionStage<RaceGroup> raceGroup);
    void updateRaceGroup(CompletionStage<RaceGroup> raceGroup);
    void deleteAllRaceGroups();
    void deleteRaceGroupByPosition(int position);
}
