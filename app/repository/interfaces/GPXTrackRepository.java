package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.GPXTrack;
import repository.GPXTrackRepositoryImpl;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(GPXTrackRepositoryImpl.class)
public interface GPXTrackRepository {
    CompletionStage<Stream<GPXTrack>> getGPXTracksByStageId(long stageId);
    CompletionStage<Stream<GPXTrack>> addGPXTracksByStageId(long stageId, List<GPXTrack> gpxTrackList);
    CompletionStage<Stream<GPXTrack>> deleteGPXTracksByStageId(long stageId);

}
