package repository;

import com.fasterxml.jackson.databind.JsonNode;
import models.Stage;
import play.db.jpa.JPAApi;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
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
    public CompletionStage<JsonNode> getAllStages() {
        return supplyAsync(() -> wrap (this::getAllStages), databaseExecutionContext);
    }

    private JsonNode getAllStages(EntityManager em){
        List<Stage> stages = em.createQuery("select s from Stage s", Stage.class).getResultList();
        return toJson(stages.stream());
    }

    @Override
    public CompletionStage<JsonNode> getStage(int stageId) {
        return supplyAsync(() -> wrap (em -> getStage(em, stageId)), databaseExecutionContext);
    }

    private JsonNode getStage(EntityManager em, int stageId){
        TypedQuery<Stage> query = em.createQuery("select s from Stage s where s.stageId = :stageId" , Stage.class);
        query.setParameter("stageId", stageId);
        return toJson(query.getSingleResult());
    }

    @Override
    public CompletionStage<JsonNode> addStage(Stage stage) {
        return supplyAsync(() -> wrap (em -> addStage(em, stage)), databaseExecutionContext);
    }

    private JsonNode addStage(EntityManager em, Stage stage){
        stage.setRace(em.merge(stage.getRace()));
        em.persist(stage);
        return toJson(stage);
    }

    @Override
    public CompletionStage<JsonNode> deleteAllStages() {
        return supplyAsync(() -> wrap(this::deleteAllStages), databaseExecutionContext);
    }

    private JsonNode deleteAllStages(EntityManager em){
        List<Stage> stages = em.createQuery("select s from Stage s", Stage.class).getResultList();
        for(Stage s : stages){
            em.remove(s);
        }
        return toJson(stages.stream());
    }

    @Override
    public CompletionStage<JsonNode> deleteStage(int stageId) {
        return supplyAsync(() -> wrap(em -> deleteStage(em, stageId)), databaseExecutionContext);
    }

    private JsonNode deleteStage(EntityManager em, int stageId){
        TypedQuery<Stage> query = em.createQuery("select s from Stage s where s.stageId = :stageId" , Stage.class);
        query.setParameter("stageId", stageId);
        Stage stage = query.getSingleResult();
        if(stage != null){
            em.remove(stage);
        }
        return toJson(stage);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
