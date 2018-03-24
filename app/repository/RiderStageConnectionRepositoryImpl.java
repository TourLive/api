package repository;

import models.RiderStageConnection;
import play.db.jpa.JPAApi;
import repository.interfaces.RiderStageConnectionRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RiderStageConnectionRepositoryImpl implements RiderStageConnectionRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final EntityManager em;

    @Inject
    public RiderStageConnectionRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
    }

    @Override
    public CompletionStage<Stream<RiderStageConnection>> getAllRiderStageConnections() {
        return supplyAsync(() -> wrap (this::getAllRiderStageConnections), databaseExecutionContext);
    }

    private Stream<RiderStageConnection> getAllRiderStageConnections(EntityManager em){
        List<RiderStageConnection> riderStageConnections = em.createQuery("select rSC from RiderStageConnection rSC", RiderStageConnection.class).getResultList();
        return riderStageConnections.stream();
    }

    @Override
    public CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderAndStage(int riderId, int stageId) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnection(em, riderId, stageId)), databaseExecutionContext);
    }

    private RiderStageConnection getRiderStageConnection(EntityManager em, int riderId, int stageId){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC where rSC.rider.riderId = :riderId and rSC.stage.stageId = :stageId" , RiderStageConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter("stageId", stageId);
        return query.getResultList().get(0);
    }

    @Override
    public void addRiderStageConnection(RiderStageConnection riderStageConnection) {
        em.getTransaction().begin();
        em.persist(riderStageConnection);
        em.getTransaction().commit();
    }

    @Override
    public void deleteAllRiderStageConnections() {
        List<RiderStageConnection> riderStageConnections = em.createQuery("select rSC from RiderStageConnection rSC", RiderStageConnection.class).getResultList();
        em.remove(riderStageConnections);
    }

    @Override
    public void deleteRiderStageConnection(int riderId, int stageId) {
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC where rSC.rider.riderId = :riderId and rSC.stage.stageId = :stageId" , RiderStageConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter("stageId", stageId);
        RiderStageConnection rSC = query.getResultList().get(0);
        if(rSC!= null){
            em.remove(rSC);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
