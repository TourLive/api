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
    private static final String STAGE_ID = "stageId";

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
        query.setParameter(STAGE_ID, stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderAndStage(long stageId, long riderId) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnection(em, stageId, riderId)), databaseExecutionContext);
    }

    private RiderStageConnection getRiderStageConnection(EntityManager em, long stageId, long riderId){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC where rSC.rider.id = :riderId and rSC.stage.id = :stageId" , RiderStageConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter(STAGE_ID, stageId);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderStartNrAndStage(long stageId, int startNr) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnectionByRiderStartNrAndStage(em, stageId, startNr)), databaseExecutionContext);
    }

    private RiderStageConnection getRiderStageConnectionByRiderStartNrAndStage(EntityManager em, long stageId, int startNr){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC where rSC.rider.startNr = :startNr and rSC.stage.id = :stageId" , RiderStageConnection.class);
        query.setParameter("startNr", startNr);
        query.setParameter(STAGE_ID, stageId);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<RiderStageConnection> getRiderStageConnection(long riderStageConnectionId) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnection(em, riderStageConnectionId)), databaseExecutionContext);
    }

    private RiderStageConnection getRiderStageConnection(EntityManager em, long riderStageConnectionId){
        return em.find(RiderStageConnection.class, riderStageConnectionId);
    }

    @Override
    public CompletionStage<Stream<RiderStageConnection>> getRiderStageConnectionsByStageWithRiderMaillots(long stageId) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnectionsByStageWithRiderMaillots(em, stageId)), databaseExecutionContext);
    }

    private Stream<RiderStageConnection> getRiderStageConnectionsByStageWithRiderMaillots(EntityManager em, long stageId){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC left join fetch rSC.riderMaillots where rSC.stage.id = :stageId" , RiderStageConnection.class);
        query.setParameter(STAGE_ID, stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<RiderStageConnection> getRiderStageConnectionByRiderStageConnectionWithRiderMaillots(long rSCId) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnectionByRiderStageConnectionWithRiderMaillots(em, rSCId)), databaseExecutionContext);
    }

    private RiderStageConnection getRiderStageConnectionByRiderStageConnectionWithRiderMaillots(EntityManager em, long rSCId){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC left join fetch rSC.riderMaillots where rSC.id = :rSCId" , RiderStageConnection.class);
        query.setParameter("rSCId", rSCId);
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
        if (riderStageConnection.getStage() == null) {
            RiderStageConnection dbRSC = entityManager.find(RiderStageConnection.class, riderStageConnection.getId());
            riderStageConnection.setRider(dbRSC.getRider());
            riderStageConnection.setRiderMaillots(dbRSC.getRiderMaillots());
            riderStageConnection.setRiderRankings(dbRSC.getRiderRankings());
            riderStageConnection.setStage(dbRSC.getStage());
        }
        entityManager.merge(riderStageConnection);
        return null;
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
        query.setParameter(STAGE_ID, stageId);
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
