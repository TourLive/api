package repository;

import models.RaceGroup;
import play.db.jpa.JPAApi;
import repository.interfaces.RaceGroupRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RaceGroupRepositoryImpl implements RaceGroupRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RaceGroupRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<RaceGroup>> getAllRaceGroups(long stageid) {
        return supplyAsync(() -> wrap (entityManager -> getAllRaceGroups(entityManager, stageid)), databaseExecutionContext);
    }

    @Override
    public CompletionStage<RaceGroup> getRaceGroupById(long id) {
        return supplyAsync(() -> wrap(em -> getRaceGroupById(em, id)), databaseExecutionContext);
    }

    private RaceGroup getRaceGroupById(EntityManager entityManager, long id) {
        TypedQuery<RaceGroup> query = entityManager.createQuery("select rG from RaceGroup rG where rG.id = :raceGroupId" , RaceGroup.class);
        query.setParameter("raceGroupId", id);
        return query.getSingleResult();
    }

    private Stream<RaceGroup> getAllRaceGroups(EntityManager em, long stageId){
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.stage.id = :stageid" , RaceGroup.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<RaceGroup> addRaceGroup(RaceGroup raceGroup) {
        return supplyAsync(() -> wrap(em -> addRaceGroup(em, raceGroup)), databaseExecutionContext);
    }

    private RaceGroup addRaceGroup(EntityManager em, RaceGroup raceGroup) {
        em.persist(raceGroup);
        return raceGroup;
    }

    @Override
    public CompletionStage<RaceGroup> updateRaceGroup(RaceGroup raceGroup) {
        return supplyAsync(() -> wrap(em -> updateRaceGroup(em, raceGroup)), databaseExecutionContext);
    }

    private RaceGroup updateRaceGroup(EntityManager entityManager, RaceGroup raceGroup) {
        RaceGroup rG = entityManager.find(RaceGroup.class, raceGroup.getId());
        rG.setActualGapTime(raceGroup.getActualGapTime());
        rG.setHistoryGapTime(raceGroup.getHistoryGapTime());
        rG.setPosition(raceGroup.getPosition());
        rG.setRaceGroupType(raceGroup.getRaceGroupType());
        rG.setTimestamp(raceGroup.getTimestamp());
        rG.setRiders(raceGroup.getRiders());
        entityManager.merge(rG);
        return rG;
    }

    @Override
    public void deleteAllRaceGroups() {
        return wrap(this::deleteAllRaceGroups);
    }

    private Stream<RaceGroup> deleteAllRaceGroups(EntityManager entityManager) {
        List<RaceGroup> raceGroups = entityManager.createQuery("select rG from RaceGroup rG", RaceGroup.class).getResultList();
        entityManager.remove(raceGroups);
        return null;
    }

    @Override
    public void deleteRaceGroupById(long id) {
        return wrap(entityManager -> deleteRaceGroupById(entityManager, id));
    }

    private RaceGroup deleteRaceGroupById(EntityManager em, long id) {
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.id = :raceGroupId" , RaceGroup.class);
        query.setParameter("raceGroupId", id);
        RaceGroup raceGroup = query.getSingleResult();
        if(raceGroup != null){
            em.remove(raceGroup);
        }
        return null;
    }
    
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
