package repository;

import models.JudgmentRiderConnection;
import models.Log;
import models.enums.NotificationType;
import play.db.jpa.JPAApi;
import repository.interfaces.JudgmentRiderConnectionRepository;
import repository.interfaces.LogRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JudgmentRiderConnectionRepositoryImpl implements JudgmentRiderConnectionRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final LogRepository logRepository;

    @Inject
    public JudgmentRiderConnectionRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, LogRepository logRepository) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.logRepository = logRepository;
    }

    @Override
    public Stream<JudgmentRiderConnection> getAllJudgmentRiderConnections() {
        return wrap(this::getAllJudgmentRiderConnections);
    }

    private Stream<JudgmentRiderConnection> getAllJudgmentRiderConnections(EntityManager em){
        List<JudgmentRiderConnection> judgmentRiderConnections = em.createQuery("select jRC from JudgmentRiderConnection jRC", JudgmentRiderConnection.class).getResultList();
        return judgmentRiderConnections.stream();
    }

    @Override
    public CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByRider(long id) {
        return supplyAsync(() -> wrap (em -> getJudgmentRiderConnectionsByRider(em, id)), databaseExecutionContext);
    }

    private Stream<JudgmentRiderConnection> getJudgmentRiderConnectionsByRider(EntityManager em, long id){
        TypedQuery<JudgmentRiderConnection> query = em.createQuery("select rSC from JudgmentRiderConnection rSC where rSC.rider.id = :id" , JudgmentRiderConnection.class);
        query.setParameter("id", id);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Stream<JudgmentRiderConnection>> getJudgmentRiderConnectionsByStage(long stageId) {
        return supplyAsync(() -> wrap (em -> getJudgmentRiderConnectionsByStage(em, stageId)), databaseExecutionContext);
    }

    private Stream<JudgmentRiderConnection> getJudgmentRiderConnectionsByStage(EntityManager em, long stageId){
        TypedQuery<JudgmentRiderConnection> query = em.createQuery("select rSC from JudgmentRiderConnection rSC where rSC.judgment.stage.id = :stageId" , JudgmentRiderConnection.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<JudgmentRiderConnection> addJudgmentRiderConnection(JudgmentRiderConnection judgmentRiderConnection, long stageId, long timestamp) {
        return supplyAsync(() -> wrap(entityManager -> addJudgmentRiderConnection(entityManager, judgmentRiderConnection, stageId, timestamp)), databaseExecutionContext);
    }

    private JudgmentRiderConnection addJudgmentRiderConnection(EntityManager entityManager, JudgmentRiderConnection judgmentRiderConnection, long stageId, long timestamp) {
        entityManager.persist(judgmentRiderConnection);
        Log log = new Log();
        JudgmentRiderConnection dbJRC = entityManager.find(JudgmentRiderConnection.class, judgmentRiderConnection.getId());
        log.setMessage(dbJRC.getJudgment().getName() +" am Kilometer: "+ dbJRC.getJudgment().getDistance() + " mit dem " + dbJRC.getRank() +". Rang");
        log.setNotificationType(NotificationType.REWARD);
        log.setRiderId(dbJRC.getRider().getRiderId());
        log.setTimestamp(new Timestamp(timestamp));
        log.setReferencedId(judgmentRiderConnection.getAppId());
        logRepository.addLog(stageId, log);
        return judgmentRiderConnection;
    }

    @Override
    public void deleteAllJudgmentRiderConnections() {
        wrap(this::deleteAllJudgmentRiderConnections);
    }

    private JudgmentRiderConnection deleteAllJudgmentRiderConnections(EntityManager entityManager) {
        List<JudgmentRiderConnection> judgmentRiderConnections = entityManager.createQuery("select jRC from JudgmentRiderConnection jRC", JudgmentRiderConnection.class).getResultList();
        entityManager.remove(judgmentRiderConnections);
        return null;
    }

    @Override
    public CompletionStage<JudgmentRiderConnection> deleteJudgmentRiderConnection(String judgmentId) {
        return supplyAsync(() -> wrap(entityManager -> deleteJudgmentRiderConnection(entityManager, judgmentId)), databaseExecutionContext);
    }

    private JudgmentRiderConnection deleteJudgmentRiderConnection(EntityManager entityManager, String judgmentId) {
        TypedQuery<JudgmentRiderConnection> query = entityManager.createQuery("select rSC from JudgmentRiderConnection rSC where rSC.appId = :judgmentId" , JudgmentRiderConnection.class);
        query.setParameter("judgmentId", judgmentId);
        JudgmentRiderConnection jRC = query.getSingleResult();
        if(jRC != null){
            entityManager.remove(jRC);
        }
        return jRC;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

}
