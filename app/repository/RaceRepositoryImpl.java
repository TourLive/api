package repository;

import models.Race;
import play.db.jpa.JPAApi;
import repository.interfaces.RaceRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

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
    public CompletionStage<Stream<Race>> getAllRaces() {
        return supplyAsync(() -> wrap (this::getAllRaces), databaseExecutionContext);
    }

    private Stream<Race> getAllRaces(EntityManager em) {
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        return races.stream();
    }

    @Override
    public CompletionStage<Race> getRace(Long raceId) {
        return supplyAsync(() -> wrap (em -> getRace(em, raceId)), databaseExecutionContext);
    }

    private Race getRace(EntityManager em, Long raceId) {
        TypedQuery<Race> query = em.createQuery("select r from Race r where r.id = :raceId" , Race.class);
        query.setParameter("raceId", raceId);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<Race> setRace(Race race) {
        return supplyAsync(() -> wrap (em -> setRace(em, race)), databaseExecutionContext);
    }

    private Race setRace(EntityManager em, Race race){
        em.persist(race);
        return race;
    }

    @Override
    public CompletionStage<Stream<Race>> deleteAllRaces() {
        return supplyAsync(() -> wrap(this::deleteAllRaces), databaseExecutionContext);
    }

    private Stream<Race> deleteAllRaces(EntityManager em){
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        for(Race r : races){
            em.remove(r);
        }
        return races.stream();
    }

    @Override
    public CompletionStage<Race> deleteRace(Long id) {
        return supplyAsync(() -> wrap(em -> deleteRace(em, id)), databaseExecutionContext);
    }

    private Race deleteRace(EntityManager em, Long id){
        TypedQuery<Race> query = em.createQuery("select r from Race r where r.id = :id" , Race.class);
        query.setParameter("id", id);
        Race race = query.getSingleResult();
        if(race != null){
            em.remove(race);
        }
        return race;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
