package repository;

import models.JudgmentRiderConnection;
import models.Rider;
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
    private final EntityManager em;

    @Inject
    public JudgmentRiderConnectionRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
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
    public CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByRider(int riderId) {
        return supplyAsync(() -> wrap (em -> getJudgmentRiderConnectionsByRider(em, riderId)), databaseExecutionContext);
    }

    private Stream<JudgmentRiderConnection> getJudgmentRiderConnectionsByRider(EntityManager em, int riderId){
        TypedQuery<JudgmentRiderConnection> query = em.createQuery("select rSC from JudgmentRiderConnection rSC where rSC.rider.riderId = :riderId" , JudgmentRiderConnection.class);
        query.setParameter("riderId", riderId);
        return query.getResultList().stream();
    }

    @Override
    public void addJudgmentRiderConnection(JudgmentRiderConnection judgmentRiderConnection) {
        em.getTransaction().begin();
        em.persist(judgmentRiderConnection);
        em.getTransaction().commit();
    }

    @Override
    public void deleteAllJudgmentRiderConnections() {
        List<JudgmentRiderConnection> judgmentRiderConnections = em.createQuery("select jRC from JudgmentRiderConnection jRC", JudgmentRiderConnection.class).getResultList();
        em.remove(judgmentRiderConnections);
    }

    @Override
    public void deleteJudgmentRiderConnectionByRiderAndJudgmentName(int riderId, String judgmentName) {
        TypedQuery<JudgmentRiderConnection> query = em.createQuery("select rSC from JudgmentRiderConnection rSC where rSC.rider.riderId = :riderId and rSC.judgment.name = :judgmentName" , JudgmentRiderConnection.class);
        query.setParameter("riderId", riderId);
        query.setParameter("judgmentName", judgmentName);
        JudgmentRiderConnection jRC = query.getResultList().get(0);
        if(jRC != null){
            em.remove(jRC);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

}
