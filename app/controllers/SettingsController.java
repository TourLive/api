package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import controllers.importutilities.Parser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.SettingRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@With(BasicAuthAction.class)
public class SettingsController extends Controller {
    private final SettingRepository settingRepository;

    @Inject
    public SettingsController(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public CompletionStage<Result> getSettings() {
        return settingRepository.getSetting().thenApplyAsync(setting -> ok(toJson(setting))).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateSettings() {
        JsonNode json = request().body().asJson();
        return CompletableFuture.completedFuture(Parser.parseSettings(json)).thenApply(settingRepository::updateSetting).thenApply(setting -> ok("success")).exceptionally(ex -> {
            Result res;
            if (ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals("NullPointerException")){
                res = badRequest("json format of setting was wrong");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
