package repository;

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
    public CompletionStage<Stage> getStage(int stageId) {
        return supplyAsync(() -> wrap (em -> getStage(em, stageId)), databaseExecutionContext);
    }

    private Stage getStage(EntityManager em, int stageId){
        TypedQuery<Stage> query = em.createQuery("select s from Stage s where s.stageId = :stageId" , Stage.class);
        query.setParameter("stageId", stageId);
        List<Stage> stages = query.getResultList();
        return stages.get(0);
    }

    @Override
    public CompletionStage<Stage> addStage(Stage stage) {
        return supplyAsync(() -> wrap (em -> addStage(em, stage)), databaseExecutionContext);
    }

    private Stage addStage(EntityManager em, Stage stage){
        try{
            stage.race = em.merge(stage.race);
            em.persist(stage);
        } catch (Exception ex){
            String m = ex.getMessage();
        }
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
    public CompletionStage<Stage> deleteStage(int stageId) {
        return supplyAsync(() -> wrap(em -> deleteStage(em, stageId)), databaseExecutionContext);
    }

    private Stage deleteStage(EntityManager em, int stageId){
        TypedQuery<Stage> query = em.createQuery("select s from Stage s where s.stageId = :stageId" , Stage.class);
        query.setParameter("stageId", stageId);
        List<Stage> stages = query.getResultList();
        em.remove(stages.get(0));
        return stages.get(0);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
