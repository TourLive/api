package repository;

import models.Race;
import play.db.jpa.JPAApi;
import repository.interfaces.RaceRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RaceRepositoryImpl implements RaceRepository{
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RaceRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Race> getRace() {
        return supplyAsync(() -> wrap (this::getRace), databaseExecutionContext);
    }

    private Race getRace(EntityManager em) {
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        return races.get(0);
    }

    @Override
    public void setRace(Race race) {
        deleteAllRaces();
        jpaApi.em().getTransaction().begin();
        jpaApi.em().persist(race);
        jpaApi.em().getTransaction().commit();
    }

    @Override
    public void deleteAllRaces() {
        List<Race> races = jpaApi.em().createQuery("select r from Race r", Race.class).getResultList();
        jpaApi.em().remove(races);
    }

    @Override
    public void deleteRace(String name) {
        Race race = jpaApi.em().find(Race.class, name);
        if(race != null){
            jpaApi.em().remove(race);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
