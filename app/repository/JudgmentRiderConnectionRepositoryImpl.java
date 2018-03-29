package repository;

import models.JudgmentRiderConnection;
import play.db.jpa.JPAApi;
import repository.interfaces.JudgmentRiderConnectionRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JudgmentRiderConnectionRepositoryImpl implements JudgmentRiderConnectionRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public JudgmentRiderConnectionRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<JudgmentRiderConnection>> getAllJudgmentRiderConnections() {
        return supplyAsync(() -> wrap (this::getAllJudgmentRiderConnections), databaseExecutionContext);
    }

    private Stream<JudgmentRiderConnection> getAllJudgmentRiderConnections(EntityManager em){
        List<JudgmentRiderConnection> judgmentRiderConnections = em.createQuery("select jRC from JudgmentRiderConnection jRC", JudgmentRiderConnection.class).getResultList();
        return judgmentRiderConnections.stream();
    }

    @Override
    public CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByRider(long id) {
        return supplyAsync(() -> wrap (em -> getJudgmentRiderConnectionsByRider(em, id)), databaseExecutionContext);
    }

    private Stream<JudgmentRiderConnection> getJudgmentRiderConnectionsByRider(EntityManager em, long id){
        TypedQuery<JudgmentRiderConnection> query = em.createQuery("select rSC from JudgmentRiderConnection rSC where rSC.rider.id = :id" , JudgmentRiderConnection.class);
        query.setParameter("id", id);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<JudgmentRiderConnection> addJudgmentRiderConnection(JudgmentRiderConnection judgmentRiderConnection) {
        return supplyAsync(() -> wrap(entityManager -> addJudgmentRiderConnection(entityManager, judgmentRiderConnection)), databaseExecutionContext);
    }

    private JudgmentRiderConnection addJudgmentRiderConnection(EntityManager entityManager, JudgmentRiderConnection judgmentRiderConnection) {
        entityManager.persist(judgmentRiderConnection);
        return judgmentRiderConnection;
    }

    @Override
    public CompletionStage<Stream<JudgmentRiderConnection>> deleteAllJudgmentRiderConnections() {
        return supplyAsync(() -> wrap(this::deleteAllJudgmentRiderConnections), databaseExecutionContext);
    }

    private Stream<JudgmentRiderConnection> deleteAllJudgmentRiderConnections(EntityManager entityManager) {
        List<JudgmentRiderConnection> judgmentRiderConnections = entityManager.createQuery("select jRC from JudgmentRiderConnection jRC", JudgmentRiderConnection.class).getResultList();
        entityManager.remove(judgmentRiderConnections);
        return judgmentRiderConnections.stream();
    }

    @Override
    public CompletionStage<JudgmentRiderConnection> deleteJudgmentRiderConnectionByRiderAndJudgmentName(long riderId, long judgmentId) {
        return supplyAsync(() -> wrap(entityManager -> deleteJudgmentRiderConnectionByRiderAndJudgmentName(entityManager, riderId, judgmentId)), databaseExecutionContext);
    }

    private JudgmentRiderConnection deleteJudgmentRiderConnectionByRiderAndJudgmentName(EntityManager entityManager, long riderId, long judgmentId) {
        TypedQuery<JudgmentRiderConnection> query = entityManager.createQuery("select rSC from JudgmentRiderConnection rSC where rSC.rider.id = :riderId and rSC.judgment.id = :judgmentId" , JudgmentRiderConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter("judgmentId", judgmentId);
        JudgmentRiderConnection jRC = query.getResultList().get(0);
        if(jRC != null){
            entityManager.remove(jRC);
        }
        return jRC;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

}
