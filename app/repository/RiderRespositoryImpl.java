package repository;

import models.Rider;
import play.db.jpa.JPAApi;
import repository.interfaces.RiderRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class RiderRespositoryImpl implements RiderRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public RiderRespositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Stream<Rider>> list() {
        return supplyAsync(() -> wrap (this::list), databaseExecutionContext);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Stream<Rider> list(EntityManager em) {
        List<Rider> riderList = em.createQuery("select r from Rider r", Rider.class).getResultList();
        return riderList.stream();
    }
}
