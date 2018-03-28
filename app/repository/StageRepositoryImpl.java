package repository;

import com.fasterxml.jackson.databind.JsonNode;
import models.Stage;
import play.db.jpa.JPAApi;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.Calendar;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.libs.Json.toJson;

public class StageRepositoryImpl implements StageRepository{
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public StageRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Stage>> getAllStages() {
        return supplyAsync(() -> wrap (this::getAllStages), databaseExecutionContext);
    }

    private Stream<Stage> getAllStages(EntityManager em){
        List<Stage> stages = em.createQuery("select s from Stage s", Stage.class).getResultList();
        return stages.stream();
    }

    @Override
    public CompletionStage<Stage> getStage(long stageId) {
        return supplyAsync(() -> wrap (em -> getStage(em, stageId)), databaseExecutionContext);
    }

    private Stage getStage(EntityManager em, long stageId){
        TypedQuery<Stage> query = em.createQuery("select s from Stage s where s.id = :stageId" , Stage.class);
        query.setParameter("stageId", stageId);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<Stage> addStage(Stage stage) {
        return supplyAsync(() -> wrap (em -> addStage(em, stage)), databaseExecutionContext);
    }

    private Stage addStage(EntityManager em, Stage stage) {
        stage.setRace(em.merge(stage.getRace()));
        em.persist(stage);
        return stage;
    }

    @Override
    public CompletionStage<Stream<Stage>> deleteAllStages() {
        return supplyAsync(() -> wrap(this::deleteAllStages), databaseExecutionContext);
    }

    private Stream<Stage> deleteAllStages(EntityManager em){
        List<Stage> stages = em.createQuery("select s from Stage s", Stage.class).getResultList();
        for(Stage s : stages){
            em.remove(s);
        }
        return stages.stream();
    }

    @Override
    public CompletionStage<Stage> deleteStage(long stageId) {
        return supplyAsync(() -> wrap(em -> deleteStage(em, stageId)), databaseExecutionContext);
    }

    private Stage deleteStage(EntityManager em, long stageId){
        TypedQuery<Stage> query = em.createQuery("select s from Stage s where s.id = :stageId" , Stage.class);
        query.setParameter("stageId", stageId);
        Stage stage = query.getSingleResult();
        if(stage != null){
            em.remove(stage);
        }
        return stage;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
