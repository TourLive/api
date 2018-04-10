package repository;

import models.RaceGroup;
import models.enums.RaceGroupType;
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
    public CompletionStage<Stream<RaceGroup>> getAllRaceGroups(long stageId) {
        return supplyAsync(() -> wrap (entityManager -> getAllRaceGroups(entityManager, stageId)), databaseExecutionContext);
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

    @Override
    public CompletionStage<RaceGroup> getRaceGroupByAppId(String id) {
        return supplyAsync(() -> wrap(em -> getRaceGroupByAppId(em, id)), databaseExecutionContext);
    }

    private RaceGroup getRaceGroupByAppId(EntityManager entityManager, String id) {
        TypedQuery<RaceGroup> query = entityManager.createQuery("select rG from RaceGroup rG where rG.appId = :raceGroupId" , RaceGroup.class);
        query.setParameter("raceGroupId", id);
        return query.getSingleResult();
    }

    @Override
    public RaceGroup getRaceGroupField() {
        return wrap(em -> getRaceGroupField(em));
    }

    private RaceGroup getRaceGroupField(EntityManager entityManager) {
        TypedQuery<RaceGroup> query = entityManager.createQuery("select rG from RaceGroup rG where rG.raceGroupType = :type" , RaceGroup.class);
        query.setParameter("type", RaceGroupType.FELD);
        return query.getSingleResult();
    }

    private Stream<RaceGroup> getAllRaceGroups(EntityManager em, long stageId){
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.stage.id = :stageId" , RaceGroup.class);
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
        entityManager.merge(raceGroup);
        return raceGroup;
    }

    @Override
    public void deleteAllRaceGroups() {
        wrap(this::deleteAllRaceGroups);
    }

    private Stream<RaceGroup> deleteAllRaceGroups(EntityManager entityManager) {
        List<RaceGroup> raceGroups = entityManager.createQuery("select rG from RaceGroup rG", RaceGroup.class).getResultList();
        for(RaceGroup rG : raceGroups){
            entityManager.remove(rG);
        }
        return null;
    }

    @Override
    public void deleteRaceGroupById(long id) {
        wrap(entityManager -> deleteRaceGroupById(entityManager, id));
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
