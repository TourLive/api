package repository;

import models.*;
import play.db.jpa.JPAApi;
import repository.interfaces.LogRepository;
import repository.interfaces.NotificationRepository;
import repository.interfaces.RiderRepository;
import repository.interfaces.RiderStageConnectionRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class NotificationRepositoryImpl implements NotificationRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final LogRepository logRepository;
    private final RiderStageConnectionRepository riderStageConnectionRepository;
    private final RiderRepository riderRepository;

    @Inject
    public NotificationRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, LogRepository logRepository, RiderStageConnectionRepository riderStageConnectionRepository, RiderRepository riderRepository) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.logRepository = logRepository;
        this.riderStageConnectionRepository = riderStageConnectionRepository;
        this.riderRepository = riderRepository;
    }

    @Override
    public CompletionStage<Stream<Notification>> getAllNotifications(long stageId) {
        return supplyAsync(() -> wrap (entityManager -> getAllNotifications(entityManager, stageId)), databaseExecutionContext);
    }

    private Stream<Notification> getAllNotifications(EntityManager em, long stageId){
        TypedQuery<Notification> query = em.createQuery("select n from Notification n where n.stage.id = :stageId" , Notification.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Stream<Notification>> getNotificationsByTimestamp(long stageId, Timestamp timestamp) {
        return supplyAsync(() -> wrap (em -> getAllNotificationsByTimestamp(em, stageId, timestamp)), databaseExecutionContext);
    }

    private Stream<Notification> getAllNotificationsByTimestamp(EntityManager em, long stageId, Timestamp timestamp){
        TypedQuery<Notification> query = em.createQuery("select n from Notification n where n.stage.id = :stageId and n.timestamp >= :timestamp" , Notification.class);
        query.setParameter("stageId", stageId);
        query.setParameter("timestamp", timestamp);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Notification> addNotification(long stageId, Notification notification) {
        supplyAsync(() -> wrap(em -> generateLogs(em, stageId, notification)), databaseExecutionContext);
        return supplyAsync(() -> wrap(em -> addNotification(em, stageId, notification)), databaseExecutionContext);
    }

    private Notification addNotification(EntityManager em, long stageId, Notification notification){
        notification.setStage(em.find(Stage.class, stageId));
        em.persist(notification);
        return notification;
    }

    private Log generateLogs(EntityManager em, long stageId, Notification notification){
        switch (notification.getNotificationType()){
            case RIDER:
                generateLogForARider(em, stageId, notification);
                break;
            case RACEGROUP:
                generateLogForARaceGroup(em, stageId, notification);
                break;
            default:
                break;
        }
        return null;
    }

    private void generateLogForARider(EntityManager em, long stageId, Notification notification){
        // Means that state of rider has changed -> ARZT, STURZ; DEFEKT; DNS; QUIT
        RiderStageConnection con = riderStageConnectionRepository.getRiderStageConnectionByRiderAndStage(stageId, Long.valueOf(notification.getReferencedId())).toCompletableFuture().join();
        Rider r = riderRepository.getRider(Long.valueOf(notification.getReferencedId()));
        Log log = new Log();
        log.setMessage(con.getTypeState().toString());
        log.setNotificationType(notification.getNotificationType());
        log.setRiderId(r.getRiderId());
        log.setStage(em.find(Stage.class, stageId));
        log.setTimestamp(notification.getTimestamp());
        logRepository.addLog(log);
    }

    private void generateLogForARaceGroup(EntityManager em, long stageId, Notification notification){
        // Means that some racegroup has changed -> check all RaceGroups and add Rider Log if rider was not in same racegroup before
    }


    @Override
    public void deleteAllNotification() {
        wrap(this::deleteAllNotification);
    }

    private Stream<Notification> deleteAllNotification(EntityManager em){
        List<Notification> notifications = em.createQuery("select n from Notification n", Notification.class).getResultList();
        for(Notification n : notifications){
            em.remove(n);
        }
        return notifications.stream();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
