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

    @Inject
    public RiderStageConnectionRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<RiderStageConnection>> getAllRiderStageConnections(long stageId) {
        return supplyAsync(() -> wrap (entityManager -> getAllRiderStageConnections(entityManager, stageId)), databaseExecutionContext);
    }

    private Stream<RiderStageConnection> getAllRiderStageConnections(EntityManager em, long stageId){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC where rSC.stage.id = :stageId" , RiderStageConnection.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderAndStage(long stageId, long riderId) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnection(em, stageId, riderId)), databaseExecutionContext);
    }

    private RiderStageConnection getRiderStageConnection(EntityManager em, long stageId, long riderId){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC where rSC.rider.id = :riderId and rSC.stage.id = :stageId" , RiderStageConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter("stageId", stageId);
        return query.getSingleResult();
    }

    @Override
    public void addRiderStageConnection(RiderStageConnection riderStageConnection) {
        wrap(entityManager -> addRiderStageConnection(entityManager,riderStageConnection));
    }

    private RiderStageConnection addRiderStageConnection(EntityManager entityManager, RiderStageConnection riderStageConnection){
        entityManager.persist(riderStageConnection);
        return null;
    }

    @Override
    public CompletionStage<RiderStageConnection> updateRiderStageConnection(RiderStageConnection riderStageConnection) {
        return supplyAsync(() -> wrap(entityManager -> updateRiderStageConnection(entityManager, riderStageConnection)));
    }

    private RiderStageConnection updateRiderStageConnection(EntityManager entityManager, RiderStageConnection riderStageConnection){
        entityManager.merge(riderStageConnection);
        return riderStageConnection;
    }

    @Override
    public void deleteAllRiderStageConnections() {
        wrap(this::deleteAllRiderStageConnections);
    }

    private Stream<RiderStageConnection> deleteAllRiderStageConnections(EntityManager entityManager){
        List<RiderStageConnection> riderStageConnections = entityManager.createQuery("select rSC from RiderStageConnection rSC", RiderStageConnection.class).getResultList();
        for(RiderStageConnection rSC : riderStageConnections){
            entityManager.remove(rSC);
        }
        return null;
    }

    @Override
    public void deleteRiderStageConnection(long stageId, long riderId) {
        wrap(entityManager -> deleteRiderStageConnection(entityManager, stageId, riderId));
    }

    private RiderStageConnection deleteRiderStageConnection(EntityManager entityManager, long stageId, long riderId) {
        TypedQuery<RiderStageConnection> query = entityManager.createQuery("select rSC from RiderStageConnection rSC where rSC.rider.id = :riderId and rSC.stage.id = :stageId" , RiderStageConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter("stageId", stageId);
        RiderStageConnection rSC = query.getResultList().get(0);
        if(rSC!= null){
            jpaApi.em().remove(rSC);
        }
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
