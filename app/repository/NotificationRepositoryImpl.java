package repository;

import com.fasterxml.jackson.databind.JsonNode;
import models.Notification;
import play.db.jpa.JPAApi;
import repository.interfaces.NotificationRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.libs.Json.toJson;

public class NotificationRepositoryImpl implements NotificationRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public NotificationRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Notification>> getAllNotifications() {
        return supplyAsync(() -> wrap (this::getAllNotifications), databaseExecutionContext);
    }

    private Stream<Notification> getAllNotifications(EntityManager em){
        List<Notification> notifications = em.createQuery("select n from Notification n", Notification.class).getResultList();
        return notifications.stream();
    }

    @Override
    public CompletionStage<Stream<Notification>> getNotificationsByTimestamp(Timestamp timestamp) {
        return supplyAsync(() -> wrap (em -> getAllNotificationsByTimestamp(em, timestamp)), databaseExecutionContext);
    }

    private Stream<Notification> getAllNotificationsByTimestamp(EntityManager em, Timestamp timestamp){
        TypedQuery<Notification> query = em.createQuery("select n from Notification n where n.timestamp >= :timestamp" , Notification.class);
        query.setParameter("timestamp", timestamp);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Notification> addNotification(Notification notification) {
        return supplyAsync(() -> wrap (em -> addNotification(em, notification)), databaseExecutionContext);
    }

    private Notification addNotification(EntityManager em, Notification notification){
        em.persist(notification);
        return notification;
    }

    @Override
    public CompletionStage<Stream<Notification>> deleteAllNotification() {
        return supplyAsync(() -> wrap (this::deleteAllNotification), databaseExecutionContext);
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
