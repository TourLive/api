package repository;

import com.fasterxml.jackson.databind.JsonNode;
import models.Rider;
import play.db.jpa.JPAApi;
import play.libs.Json;
import repository.interfaces.RiderRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.libs.Json.toJson;

public class RiderRepositoryImpl implements RiderRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RiderRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<JsonNode> getAllRiders() {
        return supplyAsync(() -> wrap (this::getAllRiders), databaseExecutionContext);
    }

    private JsonNode getAllRiders(EntityManager em){
        List<Rider> riders = em.createQuery("select r from Rider r", Rider.class).getResultList();
        return toJson(riders.stream());
    }

    @Override
    public CompletionStage<JsonNode> getRider(int riderId) {
        return supplyAsync(() -> wrap (em -> getRider(em, riderId)), databaseExecutionContext);
    }

    private JsonNode getRider(EntityManager em, int riderId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.riderId = :riderId" , Rider.class);
        query.setParameter("riderId", riderId);
        return toJson(query.getSingleResult());
    }

    @Override
    public CompletionStage<JsonNode> addRider(Rider rider) {
        return supplyAsync(() -> wrap (em -> addRider(em, rider)), databaseExecutionContext);
    }

    private JsonNode addRider(EntityManager em, Rider rider){
        em.persist(rider);
        return toJson(rider);
    }

    @Override
    public CompletionStage<JsonNode> deleteAllRiders() {
        return supplyAsync(() -> wrap (this::deleteAllRiders), databaseExecutionContext);
    }

    private JsonNode deleteAllRiders(EntityManager em){
        List<Rider> riders = em.createQuery("select r from Rider r", Rider.class).getResultList();
        for(Rider r : riders){
            em.remove(r);
        }
        return toJson(riders.stream());
    }

    @Override
    public CompletionStage<JsonNode> deleteRider(int riderId) {
        return supplyAsync(() -> wrap (em -> deleteRider(em, riderId)), databaseExecutionContext);
    }

    private JsonNode deleteRider(EntityManager em, int riderId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.riderId = :riderId" , Rider.class);
        query.setParameter("riderId", riderId);
        Rider rider = query.getSingleResult();
        if(rider != null){
            em.remove(rider);
        }
        return toJson(rider);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

}
