package repository;

import models.Rider;
import play.db.jpa.JPAApi;
import repository.interfaces.RiderRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RiderRepositoryImpl implements RiderRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RiderRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Rider>> getAllRiders() {
        return supplyAsync(() -> wrap (this::getAllRiders), databaseExecutionContext);
    }

    private Stream<Rider> getAllRiders(EntityManager em){
        List<Rider> riders = em.createQuery("select r from Rider r", Rider.class).getResultList();
        return riders.stream();
    }

    @Override
    public CompletionStage<Rider> getRider(int riderId) {
        return supplyAsync(() -> wrap (em -> getRider(em, riderId)), databaseExecutionContext);
    }

    private Rider getRider(EntityManager em, int riderId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.riderId = :riderId" , Rider.class);
        query.setParameter("riderId", riderId);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<Rider> addRider(Rider rider) {
        return supplyAsync(() -> wrap (em -> addRider(em, rider)), databaseExecutionContext);
    }

    private Rider addRider(EntityManager em, Rider rider){
        em.persist(rider);
        return rider;
    }

    @Override
    public CompletionStage<Stream<Rider>> deleteAllRiders() {
        return supplyAsync(() -> wrap (this::deleteAllRiders), databaseExecutionContext);
    }

    private Stream<Rider> deleteAllRiders(EntityManager em){
        List<Rider> riders = em.createQuery("select r from Rider r", Rider.class).getResultList();
        for(Rider r : riders){
            em.remove(r);
        }
        return riders.stream();
    }

    @Override
    public CompletionStage<Rider> deleteRider(int riderId) {
        return supplyAsync(() -> wrap (em -> deleteRider(em, riderId)), databaseExecutionContext);
    }

    private Rider deleteRider(EntityManager em, int riderId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.riderId = :riderId" , Rider.class);
        query.setParameter("riderId", riderId);
        Rider rider = query.getSingleResult();
        if(rider != null){
            em.remove(rider);
        }
        return rider;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

}
