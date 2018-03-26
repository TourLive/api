package repository;

import models.Judgment;
import play.db.jpa.JPAApi;
import repository.interfaces.JudgmentRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JudgmentRepositoryImpl implements JudgmentRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public JudgmentRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Judgment>> getAllJudgments() {
        return supplyAsync(() -> wrap (this::getAllJudgments), databaseExecutionContext);
    }

    private Stream<Judgment> getAllJudgments(EntityManager em){
        List<Judgment> judgments = em.createQuery("select j from Judgment j", Judgment.class).getResultList();
        return judgments.stream();
    }

    @Override
    public CompletionStage<Stream<Judgment>> getJudgmentsByRider(int riderId) {
        return supplyAsync(() -> wrap (em -> getJudgmentsByRider(em, riderId)), databaseExecutionContext);
    }

    private Stream<Judgment> getJudgmentsByRider(EntityManager em, int riderId){
        TypedQuery<Judgment> query = em.createQuery("select j from Judgment j where j.judgmentRiderConnections.rider.riderId = :riderId" , Judgment.class);
        query.setParameter("riderId", riderId);
        return query.getResultList().stream();
    }

    @Override
    public void addJudgment(Judgment judgment) {
        jpaApi.em().getTransaction().begin();
        jpaApi.em().persist(judgment);
        jpaApi.em().getTransaction().commit();
    }

    @Override
    public void deleteAllJudgment() {
        List<Judgment> judgments = jpaApi.em().createQuery("select j from Judgment j", Judgment.class).getResultList();
        jpaApi.em().remove(judgments);
    }

    @Override
    public void deleteJudgmentByJudgmentName(String judgmentName) {
        TypedQuery<Judgment> query = jpaApi.em().createQuery("select j from Judgment j where j.name = :judgmentName", Judgment.class);
        query.setParameter("judgmentName", judgmentName);
        Judgment j = query.getResultList().get(0);
        if (j != null) {
            jpaApi.em().remove(j);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
