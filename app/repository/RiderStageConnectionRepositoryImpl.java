package repository;

import com.fasterxml.jackson.databind.JsonNode;
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
import static play.libs.Json.toJson;

public class RiderStageConnectionRepositoryImpl implements RiderStageConnectionRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RiderStageConnectionRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<JsonNode> getAllRiderStageConnections() {
        return supplyAsync(() -> wrap (this::getAllRiderStageConnections), databaseExecutionContext);
    }

    private JsonNode getAllRiderStageConnections(EntityManager em){
        List<RiderStageConnection> riderStageConnections = em.createQuery("select rSC from RiderStageConnection rSC", RiderStageConnection.class).getResultList();
        return toJson(riderStageConnections.stream());
    }

    @Override
    public CompletionStage<JsonNode> getRiderStageConnectionByRiderAndStage(int riderId, int stageId) {
        return supplyAsync(() -> wrap (em -> getRiderStageConnection(em, riderId, stageId)), databaseExecutionContext);
    }

    private JsonNode getRiderStageConnection(EntityManager em, int riderId, int stageId){
        TypedQuery<RiderStageConnection> query = em.createQuery("select rSC from RiderStageConnection rSC where rSC.rider.riderId = :riderId and rSC.stage.stageId = :stageId" , RiderStageConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter("stageId", stageId);
        return toJson(query.getSingleResult());
    }

    @Override
    public CompletionStage<JsonNode> addRiderStageConnection(RiderStageConnection riderStageConnection) {
        jpaApi.em().getTransaction().begin();
        jpaApi.em().persist(riderStageConnection);
        jpaApi.em().getTransaction().commit();
        return null;
    }

    @Override
    public CompletionStage<JsonNode> updateRiderStageConnection(RiderStageConnection riderStageConnection) {
        RiderStageConnection pRSC = jpaApi.em().find(RiderStageConnection.class, riderStageConnection.getId());
        pRSC = riderStageConnection;
        return null;
    }

    @Override
    public CompletionStage<JsonNode> deleteAllRiderStageConnections() {
        List<RiderStageConnection> riderStageConnections = jpaApi.em().createQuery("select rSC from RiderStageConnection rSC", RiderStageConnection.class).getResultList();
        jpaApi.em().remove(riderStageConnections);
        return null;
    }

    @Override
    public CompletionStage<JsonNode> deleteRiderStageConnection(int riderId, int stageId) {
        TypedQuery<RiderStageConnection> query = jpaApi.em().createQuery("select rSC from RiderStageConnection rSC where rSC.rider.riderId = :riderId and rSC.stage.stageId = :stageId" , RiderStageConnection.class);
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
