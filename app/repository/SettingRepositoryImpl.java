package repository;

import models.Setting;
import play.db.jpa.JPAApi;
import repository.interfaces.SettingRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class SettingRepositoryImpl implements SettingRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public SettingRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletionStage<Setting> getSetting() {
        return supplyAsync(() -> wrap (this::getSetting), databaseExecutionContext);
    }

    private Setting getSetting(EntityManager entityManager) {
        return entityManager.createQuery("select s from Setting s", Setting.class).getSingleResult();
    }

    @Override
    public CompletionStage<Setting> updateSetting(Setting setting) {
        return supplyAsync(() -> wrap(entityManager -> updateSetting(entityManager, setting)), databaseExecutionContext);
    }

    private Setting updateSetting(EntityManager entityManager, Setting setting) {
        List<Setting> settingList = entityManager.createQuery("select s from Setting s", Setting.class).getResultList();
        if (settingList.isEmpty()) {
            entityManager.persist(setting);
        } else {
            Setting result = settingList.get(0);
            result.setRaceID(setting.getRaceID());
            result.setStageID(setting.getStageID());
            entityManager.merge(result);
        }
        return null;
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
