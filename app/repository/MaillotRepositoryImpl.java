package repository;

import models.Maillot;
import play.db.jpa.JPAApi;
import repository.interfaces.MaillotRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class MaillotRepositoryImpl implements MaillotRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    private final EntityManager em;

    @Inject
    public MaillotRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext, EntityManager em) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
        this.em = em;
    }

    @Override
    public CompletionStage<Stream<Maillot>> getAllMaillots() {
        return supplyAsync(() -> wrap (this::getAllMaillots), databaseExecutionContext);
    }

    private Stream<Maillot> getAllMaillots(EntityManager em){
        List<Maillot> maillots = em.createQuery("select m from Maillot m", Maillot.class).getResultList();
        return maillots.stream();
    }

    @Override
    public CompletionStage<Maillot> getMaillot(int maillotId) {
        return supplyAsync(() -> wrap (em -> getMaillot(em, maillotId)), databaseExecutionContext);
    }

    private Maillot getMaillot(EntityManager em, int maillotId){
        Maillot maillot = em.find(Maillot.class, maillotId);
        return maillot;
    }

    @Override
    public void addMaillot(CompletionStage<Maillot> maillot) {
        em.getTransaction().begin();
        em.persist(maillot);
        em.getTransaction().commit();
    }

    @Override
    public void deleteAllMaillots() {
        List<Maillot> maillots = em.createQuery("select m from Maillot m", Maillot.class).getResultList();
        em.remove(maillots);
    }

    @Override
    public void deleteMaillot(int maillotId) {
        Maillot m = em.find(Maillot.class, maillotId);
        if(m!= null){
            em.remove(m);
        }
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}