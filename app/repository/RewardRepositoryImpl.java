package repository;

import models.Reward;
import play.db.jpa.JPAApi;
import repository.interfaces.RewardRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RewardRepositoryImpl implements RewardRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final EntityManager em;

    @Inject
    public RewardRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
    }

    @Override
    public CompletionStage<Stream<Reward>> getAllRewards() {
        return supplyAsync(() -> wrap (this::getAllRewards), databaseExecutionContext);
    }

    private Stream<Reward> getAllRewards(EntityManager em){
        List<Reward> rewards = em.createQuery("select r from Reward r", Reward.class).getResultList();
        return rewards.stream();
    }

    @Override
    public void addReward(Reward reward) {
        em.getTransaction().begin();
        em.persist(reward);
        em.getTransaction().commit();
    }

    @Override
    public void deleteAllRewards() {
        List<Reward> rewards = em.createQuery("select r from Reward r", Reward.class).getResultList();
        em.remove(rewards);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
