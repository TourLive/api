package repository;

import models.Reward;
import play.db.jpa.JPAApi;
import repository.interfaces.RewardRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RewardRepositoryImpl implements RewardRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RewardRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Reward>> getAllRewards() {
        return supplyAsync(() -> wrap (this::getAllRewards), databaseExecutionContext);
    }

    @Override
    public CompletionStage<Reward> getRewardById(long id) {
        return supplyAsync(() -> wrap(entityManager -> getRewardById(entityManager, id)), databaseExecutionContext);
    }

    private Reward getRewardById(EntityManager entityManager, long id) {
        TypedQuery<Reward> query = entityManager.createQuery("select r from Reward r where r.id = :id" , Reward.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    private Stream<Reward> getAllRewards(EntityManager em){
        List<Reward> rewards = em.createQuery("select r from Reward r", Reward.class).getResultList();
        return rewards.stream();
    }

    @Override
    public CompletionStage<Reward> addReward(Reward reward) {
        return supplyAsync(() -> wrap(em -> addReward(em, reward)), databaseExecutionContext);
    }

    private Reward addReward(EntityManager entityManager, Reward reward) {
        entityManager.getTransaction().begin();
        entityManager.persist(reward);
        entityManager.getTransaction().commit();
        return reward;
    }

    @Override
    public CompletionStage<Stream<Reward>> deleteAllRewards() {
        return supplyAsync(() -> wrap(this::deleteAllRewards), databaseExecutionContext);
    }

    private Stream<Reward> deleteAllRewards(EntityManager entityManager) {
        List<Reward> rewards = entityManager.createQuery("select r from Reward r", Reward.class).getResultList();
        entityManager.remove(rewards);
        return rewards.stream();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
