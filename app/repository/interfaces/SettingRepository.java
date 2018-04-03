package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Setting;
import repository.SettingRepositoryImpl;

import java.util.concurrent.CompletionStage;

@ImplementedBy(SettingRepositoryImpl.class)
public interface SettingRepository {
    CompletionStage<Setting> getSetting();
    CompletionStage<Setting> updateSetting(Setting setting);
}