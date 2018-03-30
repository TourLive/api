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
    public void addRace(Race race) {
        wrap(entityManager -> addRace(entityManager, race));
    }

    private Race addRace(EntityManager em, Race race){
        em.merge(race);
        return null;
    }

    @Override
    public void deleteAllRaces() {
        wrap(this::deleteAllRaces);
    }

    private Stream<Race> deleteAllRaces(EntityManager em){
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        for(Race r : races){
            em.remove(r);
        }
        return null;
    }

    @Override
    public void deleteRace(Long id) {
        wrap(entityManager -> deleteRace(entityManager, id));
    }

    private Race deleteRace(EntityManager em, Long id){
        TypedQuery<Race> query = em.createQuery("select r from Race r where r.id = :id" , Race.class);
        query.setParameter("id", id);
        Race race = query.getSingleResult();
        if(race != null){
            em.remove(race);
        }
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
