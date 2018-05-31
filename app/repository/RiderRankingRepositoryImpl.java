package repository;

import models.RiderRanking;
import models.enums.RankingType;
import play.db.jpa.JPAApi;
import repository.interfaces.RiderRankingRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RiderRankingRepositoryImpl implements RiderRankingRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private static final String RANKING_TYPE = "rankingType";

    @Inject
    public RiderRankingRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<RiderRanking>> getAllRiderRankings(long riderStageConnectionId) {
        return supplyAsync(() -> wrap (entityManager -> getAllRiderRankings(entityManager, riderStageConnectionId)), databaseExecutionContext);
    }

    private Stream<RiderRanking> getAllRiderRankings(EntityManager em, long riderStageConnectionId){
        TypedQuery<RiderRanking> query = em.createQuery("select rR from RiderRanking rR where rR.riderStageConnection.id =:riderStageConnectionId" , RiderRanking.class);
        query.setParameter("riderStageConnectionId", riderStageConnectionId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Stream<RiderRanking>> getAllRiderRankingsByType(long riderStageConnectionId, String rankingType) {
        return supplyAsync(() -> wrap (em -> getAllRiderRankingsByRankingType(em, riderStageConnectionId, rankingType)), databaseExecutionContext);
    }

    private Stream<RiderRanking> getAllRiderRankingsByRankingType(EntityManager em, long riderStageConnectionId, String rankingType){
        TypedQuery<RiderRanking> query = em.createQuery("select rR from RiderRanking rR where rR.riderStageConnection.id =:riderStageConnectionId and rR.rankingType = :rankingType" , RiderRanking.class);
        query.setParameter("riderStageConnectionId", riderStageConnectionId);
        query.setParameter(RANKING_TYPE, RankingType.valueOf(rankingType));
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<RiderRanking> getRiderRankingByRiderAndType(long riderId, String rankingType) {
        return supplyAsync(() -> wrap (em -> getAllRiderRankingsByRiderIdAndRankingType(em, riderId, rankingType)), databaseExecutionContext);
    }

    private RiderRanking getAllRiderRankingsByRiderIdAndRankingType(EntityManager em, long riderId, String rankingType){
        TypedQuery<RiderRanking> query = em.createQuery("select rR from RiderRanking rR where rR.riderStageConnection.rider.id =:riderId and rR.rankingType =:rankingType" , RiderRanking.class);
        query.setParameter("riderId", riderId);
        query.setParameter(RANKING_TYPE, RankingType.valueOf(rankingType));
        return query.getSingleResult();
    }

    @Override
    public void addRiderRanking(RiderRanking riderRanking) {
        wrap(em -> addRiderRanking(em, riderRanking));
    }

    private RiderRanking addRiderRanking(EntityManager em, RiderRanking riderRanking){
        em.persist(riderRanking);
        return riderRanking;
    }

    @Override
    public CompletionStage<RiderRanking> updateRiderRanking(RiderRanking riderRanking) {
        return supplyAsync(() -> wrap(entityManager -> updateRiderRanking(entityManager, riderRanking)));
    }

    private RiderRanking updateRiderRanking(EntityManager entityManager, RiderRanking riderRanking){
        RiderRanking rR = entityManager.find(RiderRanking.class, riderRanking.getId());
        riderRanking.setRiderStageConnection(entityManager.merge(rR.getRiderStageConnection()));
        entityManager.merge(riderRanking);
        return riderRanking;
    }

    @Override
    public void deleteAllRiderRankings() {
        wrap(this::deleteAllRiderRankings);
    }

    private Stream<RiderRanking> deleteAllRiderRankings(EntityManager entityManager){
        List<RiderRanking> riderRankings = entityManager.createQuery("select rR from RiderRanking rR", RiderRanking.class).getResultList();
        for(RiderRanking rR : riderRankings){
            entityManager.remove(rR);
        }
        return null;
    }

    @Override
    public void deleteRiderRankingByRiderAndType(long riderId, RankingType rankingType) {
        wrap(entityManager -> deleteRiderRankingByRiderAndType(entityManager, riderId, rankingType));

    }

    private RiderRanking deleteRiderRankingByRiderAndType(EntityManager entityManager, long riderId, RankingType rankingType) {
        TypedQuery<RiderRanking> query =  entityManager.createQuery("select rR from RiderRanking rR where rR.riderStageConnection.rider.id =:riderId and rR.rankingType =:rankingType" , RiderRanking.class);
        query.setParameter("riderId", riderId);
        query.setParameter(RANKING_TYPE, rankingType);
        RiderRanking riderRanking = query.getSingleResult();
        if(riderRanking != null){
            entityManager.remove(riderRanking);
        }
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
