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
    private final EntityManager em;

    @Inject
    public RaceRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
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
        em.getTransaction().begin();
        em.persist(race);
        em.getTransaction().commit();
    }

    @Override
    public void deleteAllRaces() {
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        em.remove(races);
    }

    @Override
    public void deleteRace(String name) {
        Race race = em.find(Race.class, name);
        if(race != null){
            em.remove(race);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
