package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import controllers.importutilities.Parser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.cache.Cached;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.SettingRepository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Api("Settings")
public class SettingsController extends Controller {
    private final SettingRepository settingRepository;
    private static final int CACHE_DURATION = 10;

    @Inject
    public SettingsController(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @ApiOperation(value ="Get the current settings for the tourlive applications", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error on getting the current settings") })
    @Cached(key ="settings", duration = CACHE_DURATION)
    public CompletionStage<Result> getSettings() {
        return settingRepository.getSetting().thenApplyAsync(setting -> ok(toJson(setting))).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    @ApiOperation(value ="Update the current settings for the tourlive applications", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error on updating the current settings") })
    @With(BasicAuthAction.class)
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
