package repository;

import models.GPXTrack;
import models.Stage;
import play.db.jpa.JPAApi;
import repository.interfaces.GPXTrackRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class GPXTrackRepositoryImpl implements GPXTrackRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public GPXTrackRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<GPXTrack>> getGPXTracksByStageId(long stageId) {
        return supplyAsync(() -> wrap (entityManager -> getGPXTracks(entityManager, stageId)), databaseExecutionContext);
    }

    private Stream<GPXTrack> getGPXTracks(EntityManager em, long stageId){
        TypedQuery<GPXTrack> query = em.createQuery("select gpx from GPXTrack gpx where gpx.stage.id =:stageId" , GPXTrack.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Stream<GPXTrack>> addGPXTracksByStageId(long stageId, List<GPXTrack> gpxTracks) {
        return supplyAsync(() -> wrap (em -> addGPXTracks(em, stageId, gpxTracks)), databaseExecutionContext);
    }

    private Stream<GPXTrack> addGPXTracks(EntityManager em, long stageId, List<GPXTrack> gpxTracks){
        Stage stage = em.find(Stage.class, stageId);
        for(GPXTrack gpx : gpxTracks) {
            gpx.setStage(stage);
            em.persist(gpx);
        }
        return gpxTracks.stream();
    }

    @Override
    public CompletionStage<Stream<GPXTrack>> deleteGPXTracksByStageId(long stageId) {
        return supplyAsync(() -> wrap(em -> deleteGPXTracks(em, stageId)), databaseExecutionContext);
    }

    private Stream<GPXTrack> deleteGPXTracks(EntityManager em, long stageId){
        TypedQuery<GPXTrack> query = em.createQuery("select gpx from GPXTrack gpx where gpx.stage.id =:stageId" , GPXTrack.class);
        query.setParameter("stageId", stageId);
        List<GPXTrack> gpxTracks = query.getResultList();
        for(GPXTrack gpxTrack : gpxTracks){
            em.remove(gpxTrack);
        }
        return gpxTracks.stream();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
