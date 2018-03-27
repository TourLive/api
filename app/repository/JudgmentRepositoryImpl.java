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
    public CompletionStage<Stream<Judgment>> getJudgmentsByRider(long id) {
        return supplyAsync(() -> wrap (em -> getJudgmentsByRider(em, id)), databaseExecutionContext);
    }

    private Stream<Judgment> getJudgmentsByRider(EntityManager em, int id){
        TypedQuery<Judgment> query = em.createQuery("select j from Judgment j where j.judgmentRiderConnections.rider.id = :id" , Judgment.class);
        query.setParameter("id", id);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Judgment> addJudgment(Judgment judgment) {
        return supplyAsync(() -> wrap(entityManager -> addJudgment(entityManager, judgment)), databaseExecutionContext);
    }

    private Judgment addJudgment(EntityManager entityManager, Judgment judgment) {
        entityManager.persist(judgment);
        return judgment;
    }

    @Override
    public CompletionStage<Stream<Judgment>> deleteAllJudgment() {
        return supplyAsync(() -> wrap(this::deleteAllJudgment), databaseExecutionContext);
    }

    private Stream<Judgment> deleteAllJudgment(EntityManager entityManager) {
        List<Judgment> judgments = entityManager.createQuery("select j from Judgment j", Judgment.class).getResultList();
        entityManager.remove(judgments);
        return judgments.stream();
    }

    @Override
    public CompletionStage<Judgment> deleteJudgmentById(long id) {
        return supplyAsync(() -> wrap(entityManager -> deleteJudgmentById(entityManager, id)), databaseExecutionContext);
    }

    private Judgment deleteJudgmentById(EntityManager entityManager, long id) {
        TypedQuery<Judgment> query = entityManager.createQuery("select j from Judgment j where j.id = :id", Judgment.class);
        query.setParameter("id", id);
        Judgment j = query.getResultList().get(0);
        if (j != null) {
            entityManager.remove(j);
        }
        return j;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
