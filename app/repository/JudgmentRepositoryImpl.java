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
    private static final String STAGE_ID ="stageId";

    @Inject
    public JudgmentRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext =  databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Judgment>> getAllJudgments() {
        return supplyAsync(() -> wrap(this::getAllJudgments), databaseExecutionContext);
    }

    private Stream<Judgment> getAllJudgments(EntityManager em){
        List<Judgment> judgments = em.createQuery("select j from Judgment j", Judgment.class).getResultList();
        return judgments.stream();
    }

    @Override
    public CompletionStage<Stream<Judgment>> getJudgmentsByRider(long id) {
        return supplyAsync(() -> wrap(entityManager -> getJudgmentsByRider(entityManager, id)), databaseExecutionContext);
    }

    @Override
    public Judgment getJudgmentById(long id) {
        return wrap(entityManager -> getJudgmentById(entityManager, id));
    }

    private Judgment getJudgmentById(EntityManager entityManager, long id) {
        return entityManager.find(Judgment.class, id);
    }

    @Override
    public CompletionStage<Judgment> getJudgmentByIdCompleted(long id) {
        return supplyAsync(() -> wrap(entityManager -> getJudgmentByIdCompleted(entityManager, id)), databaseExecutionContext);
    }

    private Judgment getJudgmentByIdCompleted(EntityManager entityManager, long id) {
        return entityManager.find(Judgment.class, id);
    }

    private Stream<Judgment> getJudgmentsByRider(EntityManager em, long id){
        TypedQuery<Judgment> query = em.createQuery("select j from Judgment j left join JudgmentRiderConnection jRC on jRC.rider.id =:id where jRC.rider.id=:id" , Judgment.class);
        query.setParameter("id", id);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Stream<Judgment>> getJudgmentsByStage(long stageId) {
        return supplyAsync(() -> wrap(entityManager -> getJudgmentByStage(entityManager, stageId)), databaseExecutionContext);
    }

    private Stream<Judgment> getJudgmentByStage(EntityManager entityManager, long stageId) {
        TypedQuery<Judgment> query = entityManager.createQuery("select j from Judgment j where j.stage.id =:stageId" , Judgment.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }


    @Override
    public void addJudgment(Judgment judgment) {
        wrap(entityManager -> addJudgment(entityManager, judgment));
    }

    private Judgment addJudgment(EntityManager entityManager, Judgment judgment) {
        entityManager.persist(judgment);
        return null;
    }

    @Override
    public void updateJudgment(Judgment judgment){
        wrap(entityManager -> updateJudgment(entityManager, judgment));
    }

    private Judgment updateJudgment(EntityManager entityManager, Judgment judgment) {
        entityManager.merge(judgment);
        return null;
    }

    @Override
    public void deleteAllJudgment() {
        wrap(this::deleteAllJudgment);
    }

    private Judgment deleteAllJudgment(EntityManager entityManager) {
        List<Judgment> judgments = entityManager.createQuery("select j from Judgment j", Judgment.class).getResultList();
        for(Judgment j : judgments){
            entityManager.remove(j);
        }
        return null;
    }

    @Override
    public void deleteJudgmentById(long id) {
        wrap(entityManager -> deleteJudgmentById(entityManager, id));
    }

    private Judgment deleteJudgmentById(EntityManager entityManager, long id) {
        TypedQuery<Judgment> query = entityManager.createQuery("select j from Judgment j where j.id =:id", Judgment.class);
        query.setParameter("id", id);
        Judgment j = query.getResultList().get(0);
        if (j != null) {
            entityManager.remove(j);
        }
        return null;
    }

    @Override
    public CompletionStage<Stream<Judgment>> deleteAllJudgmentsOfAStage(long stageId) {
        return supplyAsync(() -> wrap(em -> deleteAllJudgmentsOfAStageAsync(em, stageId)), databaseExecutionContext);
    }

    private Stream<Judgment> deleteAllJudgmentsOfAStageAsync(EntityManager em, long stageId){
        TypedQuery<Judgment> query = em.createQuery("select j from Judgment j where j.stage.id =:stageId", Judgment.class);
        query.setParameter(STAGE_ID, stageId);
        List<Judgment> judgments = query.getResultList();
        for(Judgment j : judgments){
            em.remove(j);
        }
        return judgments.stream();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
