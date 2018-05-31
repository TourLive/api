package repository;

import models.GPXTrack;
import models.Notification;
import models.Stage;
import play.db.jpa.JPAApi;
import repository.interfaces.NotificationRepository;

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

    @Inject
    public NotificationRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
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
        return supplyAsync(() -> wrap(em -> addNotification(em, stageId, notification)), databaseExecutionContext);
    }

    private Notification addNotification(EntityManager em, long stageId, Notification notification){
        notification.setStage(em.find(Stage.class, stageId));
        em.persist(notification);
        return notification;
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

    @Override
    public CompletionStage<Stream<Notification>> deleteNotificationsByStageId(long stageId) {
        return supplyAsync(() -> wrap(em -> deleteNotifications(em, stageId)), databaseExecutionContext);
    }

    private Stream<Notification> deleteNotifications(EntityManager em, long stageId){
        TypedQuery<Notification> query = em.createQuery("select gpx from Notification n where n.stage.id =:stageId" , Notification.class);
        query.setParameter("stageId", stageId);
        List<Notification> notifications = query.getResultList();
        for(Notification notification : notifications){
            em.remove(notification);
        }
        return notifications.stream();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
