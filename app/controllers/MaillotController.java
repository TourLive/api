package controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.Maillot;
import models.MaillotDTO;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.MaillotRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("Maillot")
public class MaillotController extends Controller {
    private final MaillotRepository maillotRepository;
    private static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";
    private static final String NULLPOINTEREXCEPTION = "NullPointerException";

    @Inject
    public MaillotController(MaillotRepository maillotRepository) { this.maillotRepository = maillotRepository; }

    @ApiOperation(value ="get all maillots of a stage", response = Maillot.class)
    public CompletionStage<Result> getMaillots(Long stageId) {
        return maillotRepository.getAllMaillots(stageId).thenApplyAsync(maillots -> {
            List<MaillotDTO> maillotDTOList = new ArrayList<>();
            for (Maillot m : maillots.collect(Collectors.toList())) {
                maillotDTOList.add(new MaillotDTO(m));
            }
            return ok(toJson(maillotDTOList));
        }).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(INDEXOUTOFBOUNDEXCEPETION)){
                res = badRequest("No maillots are set in DB for this stage.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @ApiOperation(value ="get maillot by id", response = Maillot.class)
    public CompletionStage<Result> getMaillot(Long maillotId) {
        return maillotRepository.getMaillot(maillotId).thenApplyAsync(maillot -> ok(toJson(new MaillotDTO(maillot)))).exceptionally(ex -> {
            Result res;
            if(ExceptionUtils.getRootCause(ex).getClass().getSimpleName().equals(NULLPOINTEREXCEPTION)){
                res = badRequest("No specific maillot is set in DB for this id.");
            } else {
                res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
