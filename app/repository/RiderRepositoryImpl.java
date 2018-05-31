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
    private static final String RIDER_ID = "riderId";

    @Inject
    public RiderRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public List<Rider> getAllRiders() {
        return wrap(this::getAllRiders);
    }

    private List<Rider> getAllRiders(EntityManager em){
        return em.createQuery("select r from Rider r", Rider.class).getResultList();
    }

    @Override
    public Rider getRiderByCnlabId(long riderId) {
        return wrap(entityManager -> getRiderByCnlabId(entityManager, riderId));
    }

    private Rider getRiderByCnlabId(EntityManager em, long riderId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.riderId =:riderId" , Rider.class);
        query.setParameter(RIDER_ID, riderId);
        return query.getSingleResult();
    }

    @Override
    public Rider getRider(long riderId) {
        return wrap(entityManager -> getRider(entityManager, riderId));
    }

    private Rider getRider(EntityManager em, long riderId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.id =:riderId" , Rider.class);
        query.setParameter(RIDER_ID, riderId);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<Stream<Rider>> getAllRiders(long stageid) {
        return supplyAsync(() -> wrap(entityManager -> getAllRiders(entityManager, stageid)), databaseExecutionContext);
    }

    private Stream<Rider> getAllRiders(EntityManager entityManager, long stageid) {
        TypedQuery<Rider> query = entityManager.createQuery("select r from Rider r inner join RiderStageConnection rc on r.id = rc.rider.id where rc.stage.id =:stageid" , Rider.class);
        query.setParameter("stageid", stageid);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Rider> getRiderAsync(long riderId) {
        return supplyAsync(() -> wrap(entityManager -> getRiderAsync(entityManager, riderId)), databaseExecutionContext);
    }

    private Rider getRiderAsync(EntityManager em, long riderId){
        return em.find(Rider.class, riderId);
    }

    @Override
    public void addRider(Rider rider) {
        wrap(entityManager -> addRider(entityManager, rider));
    }

    private Rider addRider(EntityManager em, Rider rider){
        em.persist(rider);
        return null;
    }

    @Override
    public void deleteAllRiders() {
        wrap(this::deleteAllRiders);
    }

    private Rider deleteAllRiders(EntityManager em){
        List<Rider> riders = em.createQuery("select r from Rider r", Rider.class).getResultList();
        for(Rider r : riders){
            em.remove(r);
        }
        return null;
    }

    @Override
    public void deleteRider(long riderId) {
        wrap(entityManager -> deleteRider(entityManager, riderId));
    }

    private Rider deleteRider(EntityManager em, long riderId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.id =:riderId" , Rider.class);
        query.setParameter(RIDER_ID, riderId);
        Rider rider = query.getSingleResult();
        if(rider != null){
            em.remove(rider);
        }
        return null;
    }

    @Override
    public CompletionStage<Stream<Rider>> deleteAllRidersOfARace(long raceId) {
        return supplyAsync(() -> wrap(em -> deleteAllRidersOfARaceAsync(em, raceId)), databaseExecutionContext);
    }

    private Stream<Rider> deleteAllRidersOfARaceAsync(EntityManager em, long raceId){
        TypedQuery<Rider> query = em.createQuery("select r from Rider r where r.raceId =:raceId", Rider.class);
        query.setParameter("raceId", raceId);
        List<Rider> riders = query.getResultList();
        for(Rider r : riders){
            em.remove(r);
        }
        return riders.stream();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

}
