package repository;

import models.Judgment;
import play.db.jpa.JPAApi;
import repository.interfaces.JudgmentRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class JudgmentRepositoryImpl implements JudgmentRepository {
    private final JPAApi jpaApi;

    @Inject
    public JudgmentRepositoryImpl(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }

    @Override
    public Stream<Judgment> getAllJudgments() {
        return wrap(this::getAllJudgments);
    }

    private Stream<Judgment> getAllJudgments(EntityManager em){
        List<Judgment> judgments = em.createQuery("select j from Judgment j", Judgment.class).getResultList();
        return judgments.stream();
    }

    @Override
    public Stream<Judgment> getJudgmentsByRider(long id) {
        return wrap(entityManager -> getJudgmentsByRider(entityManager, id));
    }

    private Stream<Judgment> getJudgmentsByRider(EntityManager em, long id){
        TypedQuery<Judgment> query = em.createQuery("select j from Judgment j where j.judgmentRiderConnections.rider.id = :id" , Judgment.class);
        query.setParameter("id", id);
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
    public void deleteAllJudgment() {
        wrap(this::deleteAllJudgment);
    }

    private Judgment deleteAllJudgment(EntityManager entityManager) {
        List<Judgment> judgments = entityManager.createQuery("select j from Judgment j", Judgment.class).getResultList();
        entityManager.remove(judgments);
        return null;
    }

    @Override
    public void deleteJudgmentById(long id) {
        wrap(entityManager -> deleteJudgmentById(entityManager, id));
    }

    private Judgment deleteJudgmentById(EntityManager entityManager, long id) {
        TypedQuery<Judgment> query = entityManager.createQuery("select j from Judgment j where j.id = :id", Judgment.class);
        query.setParameter("id", id);
        Judgment j = query.getResultList().get(0);
        if (j != null) {
            entityManager.remove(j);
        }
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
