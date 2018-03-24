package repository;

import models.Rider;
import play.db.jpa.JPAApi;
import repository.interfaces.RiderRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RiderRepositoryImpl implements RiderRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final EntityManager em;

    @Inject
    public RiderRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
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
        Rider rider = em.find(Rider.class, riderId);
        return rider;
    }

    @Override
    public void addRider(CompletionStage<Rider> rider) {
        em.getTransaction().begin();
        em.persist(rider);
        em.getTransaction().commit();
    }

    @Override
    public void deleteAllRiders() {
        List<Rider> riders = em.createQuery("select r from Rider r", Rider.class).getResultList();
        em.remove(riders);
    }

    @Override
    public void deleteRider(int riderId) {
        Rider pRider = em.find(Rider.class, riderId);
        if(pRider != null){
            em.remove(pRider);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

}
