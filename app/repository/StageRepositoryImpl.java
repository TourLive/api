package repository;

import models.Stage;
import play.db.jpa.JPAApi;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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
        Stage stage = em.find(Stage.class, stageId);
        return stage;
    }

    @Override
    public void addStage(CompletionStage<Stage> stage) {
        jpaApi.em().getTransaction().begin();
        jpaApi.em().persist(stage);
        jpaApi.em().getTransaction().commit();
    }

    @Override
    public void deleteAllStage() {
        List<Stage> stages = jpaApi.em().createQuery("select s from Stage s", Stage.class).getResultList();
        jpaApi.em().remove(stages);
    }

    @Override
    public void deleteStage(int stageId) {
        Stage pStage = jpaApi.em().find(Stage.class, stageId);
        if(pStage != null){
            jpaApi.em().remove(pStage);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
