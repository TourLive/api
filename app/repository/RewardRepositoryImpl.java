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

    @Inject
    public RewardRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
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
        jpaApi.em().getTransaction().begin();
        jpaApi.em().persist(reward);
        jpaApi.em().getTransaction().commit();
    }

    @Override
    public void deleteAllRewards() {
        List<Reward> rewards = jpaApi.em().createQuery("select r from Reward r", Reward.class).getResultList();
        jpaApi.em().remove(rewards);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
