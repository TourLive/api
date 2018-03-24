package repository;

import models.Notification;
import models.RiderRanking;
import models.enums.RankingType;
import play.db.jpa.JPAApi;
import repository.interfaces.RiderRankingRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RiderRankingRepositoryImpl implements RiderRankingRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final EntityManager em;

    @Inject
    public RiderRankingRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
    }

    @Override
    public CompletionStage<Stream<RiderRanking>> getAllRiderRankings() {
        return supplyAsync(() -> wrap (this::getAllRiderRankings), databaseExecutionContext);
    }

    private Stream<RiderRanking> getAllRiderRankings(EntityManager em){
        List<RiderRanking> riderRankings = em.createQuery("select rR from RiderRanking rR", RiderRanking.class).getResultList();
        return riderRankings.stream();
    }

    @Override
    public CompletionStage<Stream<RiderRanking>> getAllRiderRankingsByType(RankingType rankingType) {
        return supplyAsync(() -> wrap (em -> getAllRiderRankingsByRankingType(em, rankingType)), databaseExecutionContext);
    }

    private Stream<RiderRanking> getAllRiderRankingsByRankingType(EntityManager em, RankingType rankingType){
        TypedQuery<RiderRanking> query = em.createQuery("select rR from RiderRanking rR where rR.rankingType = :rankingType" , RiderRanking.class);
        query.setParameter("rankingType", rankingType);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<RiderRanking> getRankingByRiderAndType(int riderId, RankingType rankingType) {
        return supplyAsync(() -> wrap (em -> getAllRiderRankingsByRiderIdAndRankingType(em, riderId, rankingType)), databaseExecutionContext);
    }

    private RiderRanking getAllRiderRankingsByRiderIdAndRankingType(EntityManager em, int riderId, RankingType rankingType){
        TypedQuery<RiderRanking> query = em.createQuery("select rR from RiderRanking rR where rR.riderStageConnection.rider.riderId = :riderId and rR.rankingType = :rankingType" , RiderRanking.class);
        query.setParameter("riderId", riderId);
        query.setParameter("rankingType", rankingType);
        return query.getResultList().get(0);
    }

    @Override
    public void addRiderRanking(RiderRanking riderRanking) {
        em.getTransaction().begin();
        em.persist(riderRanking);
        em.getTransaction().commit();
    }

    @Override
    public void updateRiderRanking(RiderRanking riderRanking) {
        RiderRanking rR = em.find(RiderRanking.class, riderRanking.id);
        rR = riderRanking;
    }

    @Override
    public void deleteAllRiderRankings() {
        List<RiderRanking> riderRankings = em.createQuery("select rR from RiderRanking rR", RiderRanking.class).getResultList();
        em.remove(riderRankings);
    }

    @Override
    public void deleteRiderRankingByRiderAndType(int riderId, RankingType rankingType) {
        TypedQuery<RiderRanking> query = em.createQuery("select rR from RiderRanking rR where rR.riderStageConnection.rider.riderId = :riderId and rR.rankingType = :rankingType" , RiderRanking.class);
        query.setParameter("riderId", riderId);
        query.setParameter("rankingType", rankingType);
        RiderRanking riderRanking = query.getResultList().get(0);
        if(riderRanking != null){
            em.remove(riderRanking);
        }
    }
    
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
