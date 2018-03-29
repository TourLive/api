package controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.MaillotRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class MaillotController extends Controller {
    private final MaillotRepository maillotRepository;

    @Inject
    public MaillotController(MaillotRepository maillotRepository) { this.maillotRepository = maillotRepository; }

    public CompletionStage<Result> getMaillots(long stageId) {
        return maillotRepository.getAllMaillots(stageId).thenApplyAsync(maillots -> ok(toJson(maillots.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No notifications are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    public CompletionStage<Result> getMaillot(Long stageId, Long maillotId) {
        return maillotRepository.getMaillot(stageId, maillotId).thenApplyAsync(maillot -> ok(toJson(maillot))).exceptionally(ex -> {
            Result res;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No notifications are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
