package repository;

import models.Log;
import models.RaceGroup;
import models.Rider;
import models.enums.NotificationType;
import models.enums.RaceGroupType;
import play.db.jpa.JPAApi;
import repository.interfaces.LogRepository;
import repository.interfaces.RaceGroupRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RaceGroupRepositoryImpl implements RaceGroupRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private static final String RACEGROUP_ID = "raceGroupId";
    private final LogRepository logRepository;
    private static final String STAGE_ID = "stageId";

    @Inject
    public RaceGroupRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, LogRepository logRepository) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.logRepository = logRepository;
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
        TypedQuery<RaceGroup> query = entityManager.createQuery("select rG from RaceGroup rG where rG.id =:raceGroupId" , RaceGroup.class);
        query.setParameter(RACEGROUP_ID, id);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<RaceGroup> getRaceGroupByAppId(String id) {
        return supplyAsync(() -> wrap(em -> getRaceGroupByAppId(em, id)), databaseExecutionContext);
    }

    private RaceGroup getRaceGroupByAppId(EntityManager entityManager, String id) {
        TypedQuery<RaceGroup> query = entityManager.createQuery("select rG from RaceGroup rG where rG.appId =:raceGroupId" , RaceGroup.class);
        query.setParameter(RACEGROUP_ID, id);
        return query.getSingleResult();
    }

    @Override
    public RaceGroup getRaceGroupField(long stageId) {
        return wrap(em -> getRaceGroupField(em, stageId));
    }

    private RaceGroup getRaceGroupField(EntityManager entityManager, long stageId) {
        TypedQuery<RaceGroup> query = entityManager.createQuery("select rG from RaceGroup rG where rG.raceGroupType =:type and rG.stage.id =:stageId" , RaceGroup.class);
        query.setParameter("type", RaceGroupType.FELD);
        query.setParameter(STAGE_ID, stageId);
        return query.getSingleResult();
    }

    private Stream<RaceGroup> getAllRaceGroups(EntityManager em, long stageId){
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.stage.id =:stageId" , RaceGroup.class);
        query.setParameter(STAGE_ID, stageId);
        return query.getResultList().stream();
    }

    @Override
    public List<RaceGroup> getAllRaceGroupsSync(long stageId) {
        return wrap (entityManager -> getAllRaceGroupsSync(entityManager, stageId)).collect(Collectors.toList());
    }

    private Stream<RaceGroup> getAllRaceGroupsSync(EntityManager em, long stageId){
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.stage.id =:stageId" , RaceGroup.class);
        query.setParameter(STAGE_ID, stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<RaceGroup> addRaceGroup(RaceGroup raceGroup, long timestamp) {
        return supplyAsync(() -> wrap(em -> addRaceGroup(em, raceGroup)), databaseExecutionContext)
                .thenApplyAsync(dbRaceGroup -> {generateAddRaceGroupLogs(dbRaceGroup, timestamp); return dbRaceGroup;});
    }

    private RaceGroup addRaceGroup(EntityManager em, RaceGroup raceGroup) {
        em.persist(raceGroup);
        return raceGroup;
    }

    private void generateAddRaceGroupLogs(RaceGroup raceGroup, long timestamp){
        // Means that some racegroup has been added -> Log RaceGroup for all Riders
        for(Rider r : raceGroup.getRiders()){
            if(raceGroup.getRaceGroupType() == RaceGroupType.NORMAL){
                createLogAndPersist("Gruppe " + raceGroup.getPosition(), raceGroup.getStage().getId(), r.getRiderId(), raceGroup.getAppId(), NotificationType.RACEGROUP, timestamp);
            } else {
                createLogAndPersist(raceGroup.getRaceGroupType().toString(), raceGroup.getStage().getId(), r.getRiderId(), raceGroup.getAppId(), NotificationType.RACEGROUP, timestamp);
            }
        }
    }

    private void createLogAndPersist(String message, long stageId, long riderId, String referencedId, NotificationType type, long timestamp){
        Log log = new Log();
        log.setMessage(message);
        log.setNotificationType(type);
        log.setRiderId(riderId);
        log.setTimestamp(new Timestamp(timestamp));
        log.setReferencedId(referencedId);
        logRepository.addLog(stageId, log);
    }

    @Override
    public CompletionStage<RaceGroup> updateRaceGroup(RaceGroup raceGroup, long timestamp) {
        return supplyAsync(() -> wrap(em -> updateRaceGroup(em, raceGroup)), databaseExecutionContext)
                .thenApplyAsync(dbRaceGroup -> {generateUpdateRaceGroupLogs(dbRaceGroup, timestamp); return dbRaceGroup;});
    }

    private void generateUpdateRaceGroupLogs(RaceGroup raceGroup, long timestamp){
        // Means that some racegroup has been updated -> Log RaceGroup for all Riders
        List<Rider> riders = raceGroup.getRiders();
        for(Rider r : riders){
            Log lastLogForRider = logRepository.getLastLogOfAStageAndRiderNotificationType(raceGroup.getStage().getId(), r.getRiderId(), NotificationType.RACEGROUP);
            if(lastLogForRider != null && lastLogForRider.getReferencedId().equals(raceGroup.getAppId())){
                continue;
            }
            // no log present yet or rider has changed racegroup -> create log
            if(raceGroup.getRaceGroupType() == RaceGroupType.NORMAL){
                createLogAndPersist("Gruppe " + raceGroup.getPosition(), raceGroup.getStage().getId(), r.getRiderId(), raceGroup.getAppId(), NotificationType.RACEGROUP, timestamp);
            } else {
                createLogAndPersist(raceGroup.getRaceGroupType().toString(), raceGroup.getStage().getId(), r.getRiderId(), raceGroup.getAppId(), NotificationType.RACEGROUP, timestamp);
            }
        }
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
        query.setParameter(RACEGROUP_ID, id);
        RaceGroup raceGroup = query.getSingleResult();
        if(raceGroup != null){
            em.remove(raceGroup);
        }
        return null;
    }

    @Override
    public CompletionStage<Stream<RaceGroup>> deleteAllRaceGroupsOfAStage(long stageId) {
        return supplyAsync(() -> wrap(em -> deleteAllRaceGroupsOfAStageAsync(em, stageId)), databaseExecutionContext);
    }

    private Stream<RaceGroup> deleteAllRaceGroupsOfAStageAsync(EntityManager em, long stageId){
        TypedQuery<RaceGroup> query = em.createQuery("select rG from RaceGroup rG where rG.stage.id =:stageId", RaceGroup.class);
        query.setParameter(STAGE_ID, stageId);
        List<RaceGroup> raceGroups = query.getResultList();
        for(RaceGroup rG : raceGroups){
            em.remove(rG);
        }
        return raceGroups.stream();
    }
    
    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
