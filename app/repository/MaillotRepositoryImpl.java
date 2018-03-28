package repository;

import models.Maillot;
import play.db.jpa.JPAApi;
import repository.interfaces.MaillotRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class MaillotRepositoryImpl implements MaillotRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;
    @Inject
    public MaillotRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Maillot>> getAllMaillots(long stageId) {
        return supplyAsync(() -> wrap (entityManager -> getAllMaillots(entityManager, stageId)), databaseExecutionContext);
    }

    private Stream<Maillot> getAllMaillots(EntityManager em, long stageId){
        TypedQuery<Maillot> query = em.createQuery("select m from Maillot m where m.stage.id = :stageId" , Maillot.class);
        query.setParameter("stageId", stageId);
        return query.getResultList().stream();
    }

    @Override
    public CompletionStage<Maillot> getMaillot(long stageId, long maillotId) {
        return supplyAsync(() -> wrap (em -> getMaillot(em, stageId, maillotId)), databaseExecutionContext);
    }

    private Maillot getMaillot(EntityManager em, long stageId, long maillotId){
        TypedQuery<Maillot> query = em.createQuery("select m from Maillot m where m.stage.id = :stageId and m.id = :maillotId" , Maillot.class);
        query.setParameter("stageId", stageId);
        query.setParameter("maillotId", maillotId);
        return query.getSingleResult();
    }

    @Override
    public void addMaillot(Maillot maillot) {
        wrap(entityManager -> addRiderStageConnection(entityManager, maillot));
    }

    private Maillot addRiderStageConnection(EntityManager entityManager, Maillot maillot){
        entityManager.persist(maillot);
        return null;
    }

    @Override
    public void deleteAllMaillots() {
        wrap(this::deleteAllRiderStageConnections);
    }

    private Stream<Maillot> deleteAllRiderStageConnections(EntityManager entityManager){
        List<Maillot> maillots = entityManager.createQuery("select m from Maillot m", Maillot.class).getResultList();
        for(Maillot m : maillots){
            entityManager.remove(m);
        }
        return null;
    }

    @Override
    public void deleteMaillot(long stageId, long maillotId) {
        wrap(entityManager -> deleteMaillot(entityManager, stageId, maillotId));
    }

    private Maillot deleteMaillot(EntityManager entityManager, long stageId, long maillotId) {
        TypedQuery<Maillot> query = entityManager.createQuery("select m from Maillot m where m.stage.id = :stageId and m.id = :maillotId" , Maillot.class);
        query.setParameter("maillotId", maillotId);
        query.setParameter("stageId", stageId);
        Maillot m = query.getSingleResult();
        if(m!= null){
            entityManager.remove(m);
        }
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
