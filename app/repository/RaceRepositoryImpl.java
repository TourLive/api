package repository;

import com.fasterxml.jackson.databind.JsonNode;
import models.Race;
import play.db.jpa.JPAApi;
import play.libs.Json;
import repository.interfaces.RaceRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.libs.Json.toJson;

public class RaceRepositoryImpl implements RaceRepository{
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RaceRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<JsonNode> getAllRaces() {
        return supplyAsync(() -> wrap (this::getAllRaces), databaseExecutionContext);
    }

    private JsonNode getAllRaces(EntityManager em) {
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        return toJson(races);
    }

    @Override
    public CompletionStage<JsonNode> getRace(int raceId) {
        return supplyAsync(() -> wrap (em -> getRace(em, raceId)), databaseExecutionContext);
    }

    private JsonNode getRace(EntityManager em, int raceId) {
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        TypedQuery<Race> query = em.createQuery("select r from Race r where r.raceId = :raceId" , Race.class);
        query.setParameter("raceId", raceId);
        return toJson(query.getSingleResult());
    }

    public CompletionStage<Race> getDbRace(int raceId){
        return supplyAsync(() -> wrap(em -> getDbRace(em, raceId)), databaseExecutionContext);
    }

    private Race getDbRace(EntityManager em, int raceId){
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        TypedQuery<Race> query = em.createQuery("select r from Race r where r.raceId = :raceId" , Race.class);
        query.setParameter("raceId", raceId);
        return query.getSingleResult();
    }

    @Override
    public CompletionStage<JsonNode> setRace(Race race) {
        return supplyAsync(() -> wrap (em -> setRace(em, race)), databaseExecutionContext);
    }

    private JsonNode setRace(EntityManager em, Race race){
        em.persist(race);
        return toJson(race);
    }

    @Override
    public CompletionStage<JsonNode> deleteAllRaces() {
        return supplyAsync(() -> wrap(this::deleteAllRaces), databaseExecutionContext);
    }

    private JsonNode deleteAllRaces(EntityManager em){
        List<Race> races = em.createQuery("select r from Race r", Race.class).getResultList();
        for(Race r : races){
            em.remove(r);
        }
        return toJson(races.stream());
    }

    @Override
    public CompletionStage<JsonNode> deleteRace(String name) {
        return supplyAsync(() -> wrap(em -> deleteRace(em, name)), databaseExecutionContext);
    }

    private JsonNode deleteRace(EntityManager em, String name){
        TypedQuery<Race> query = em.createQuery("select r from Race r where r.name = :name" , Race.class);
        query.setParameter("name", name);
        Race race = query.getSingleResult();
        if(race != null){
            em.remove(race);
        }
        return toJson(race);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
