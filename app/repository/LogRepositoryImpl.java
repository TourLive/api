package repository;

import models.Log;
import models.enums.NotificationType;
import play.db.jpa.JPAApi;
import repository.interfaces.LogRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class LogRepositoryImpl implements LogRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public LogRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Log>> getAllLogsOfAStage(long stageId) {
        return supplyAsync(() -> wrap (entityManager -> getAllLogsOfAStageAsync(entityManager, stageId)), databaseExecutionContext);
    }

    private Stream<Log> getAllLogsOfAStageAsync(EntityManager em, long stageId){
        TypedQuery<Log> query = em.createQuery("select l from Log l where l.stage.id =:stageId" , Log.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Stream<Log>> getAllLogsOfAStageAndRider(long stageId, long riderId) {
        return supplyAsync(() -> wrap (entityManager -> getAllLogsOfAStageAndRiderAsync(entityManager, stageId, riderId)), databaseExecutionContext);
    }

    private Stream<Log> getAllLogsOfAStageAndRiderAsync(EntityManager em, long stageId, long riderId){
        TypedQuery<Log> query = em.createQuery("select l from Log l where l.stage.id =:stageId and l.riderId =: riderId" , Log.class);
        query.setParameter("stageId", stageId);
        query.setParameter("riderId", riderId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Stream<Log>> getAllLogsOfAStageAndRiderAndNotificationType(long stageId, long riderId, NotificationType type) {
        return supplyAsync(() -> wrap (entityManager -> getAllLogsOfAStageAndRiderAndNotificationTypeAsync(entityManager, stageId, riderId, type)), databaseExecutionContext);
    }

    private Stream<Log> getAllLogsOfAStageAndRiderAndNotificationTypeAsync(EntityManager em, long stageId, long riderId, NotificationType type){
        TypedQuery<Log> query = em.createQuery("select l from Log l where l.stage.id =:stageId and l.riderId =: riderId and l.notificationType =: type" , Log.class);
        query.setParameter("stageId", stageId);
        query.setParameter("riderId", riderId);
        query.setParameter("type", type);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Log> addLog(Log log) {
        return supplyAsync(() -> wrap(em -> addLogAsync(em, log)), databaseExecutionContext);
    }

    private Log addLogAsync(EntityManager em, Log log){
        em.persist(log);
        return log;
    }

    @Override
    public CompletionStage<Stream<Log>> deleteAllLogsOfAStage(long stageId) {
        return supplyAsync(() -> wrap(em -> deleteAllLogsOfAStageAsync(em, stageId)), databaseExecutionContext);
    }

    private Stream<Log> deleteAllLogsOfAStageAsync(EntityManager em, long stageId){
        TypedQuery<Log> query = em.createQuery("select l from Log l where l.stage.id =: stageId", Log.class);
        query.setParameter("stageId", stageId);
        List<Log> logs = query.getResultList();
        for(Log l : logs){
            em.remove(l);
        }
        return logs.stream();
    }

    @Override
    public CompletionStage<Stream<Log>> deleteAllLogs() {
        return supplyAsync(() -> wrap(this::deleteAllLogsAsync));
    }

    private Stream<Log> deleteAllLogsAsync(EntityManager em){
        List<Log> logs = em.createQuery("select l from Log l", Log.class).getResultList();
        for(Log l : logs){
            em.remove(l);
        }
        return logs.stream();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
