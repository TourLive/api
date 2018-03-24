package repository;

import models.Judgment;
import models.RaceGroup;
import play.db.jpa.JPAApi;
import repository.interfaces.RaceGroupRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RaceGroupRepositoryImpl implements RaceGroupRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final EntityManager em;

    @Inject
    public RaceGroupRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
    }

    @Override
    public CompletionStage<Stream<RaceGroup>> getAllRaceGroups() {
        return supplyAsync(() -> wrap (this::getAllRaceGroups), databaseExecutionContext);
    }

    private Stream<RaceGroup> getAllRaceGroups(EntityManager em){
        List<RaceGroup> raceGroups = em.createQuery("select rG from RaceGroup rG", RaceGroup.class).getResultList();
        return raceGroups.stream();
    }

    @Override
    public CompletionStage<Stream<RaceGroup>> getRaceGroupsByTimestamp(Timestamp timestamp) {
        return supplyAsync(() -> wrap (em -> getRaceGroupsByTimestamp(em, timestamp)), databaseExecutionContext);
    }

    private Stream<RaceGroup> getRaceGroupsByTimestamp(EntityManager em, Timestamp timestamp){
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.timestamp >= :timestamp" , RaceGroup.class);
        query.setParameter("timestamp", timestamp);
        return query.getResultList().stream();
    }

    @Override
    public void addRaceGroup(RaceGroup raceGroup) {
        em.getTransaction().begin();
        em.persist(raceGroup);
        em.getTransaction().commit();
    }

    @Override
    public void updateRaceGroup(RaceGroup raceGroup) {
        RaceGroup rG = em.find(RaceGroup.class, raceGroup.id);
        rG = raceGroup;
    }

    @Override
    public void deleteAllRaceGroups() {
        List<RaceGroup> raceGroups = em.createQuery("select rG from RaceGroup rG", RaceGroup.class).getResultList();
        em.remove(raceGroups);
    }

    @Override
    public void deleteRaceGroupByPosition(int position) {
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.position = :position", RaceGroup.class);
        query.setParameter("position", position);
        RaceGroup rG = query.getResultList().get(0);
        if (rG != null) {
            em.remove(rG);
        }
    }
    
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
