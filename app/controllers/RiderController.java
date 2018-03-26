package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Rider;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.core.j.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RiderRepository;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class RiderController extends Controller {
    private final RiderRepository riderRepository;

    @Inject
    public RiderController(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public CompletionStage<Result> getRiders() {
        return riderRepository.getAllRiders().thenApplyAsync(riders -> {
            return ok(riders);
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "IndexOutOfBoundsException":
                    res = badRequest("No riders are set in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    public CompletionStage<Result> getRider(int riderId){
        return riderRepository.getRider(riderId).thenApplyAsync(rider -> {
            return ok(rider);
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("No rider with id: " + riderId + " is available in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addRider(){
        JsonNode json = request().body().asJson();
        return parseRider(json).thenApply(rider -> riderRepository.addRider(rider)).thenApply(rider -> {
            return ok(rider + " has been added");
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NullPointerException":
                    res = badRequest("json format of rider was wrong");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }

    private CompletableFuture<Rider> parseRider(JsonNode json){
        CompletableFuture<Rider> completableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try{
                Rider rider = new Rider();
                rider.country = json.findPath("country").textValue();
                rider.isUnkown = json.findPath("isUnkown").booleanValue();
                rider.name = json.findPath("name").textValue();
                rider.riderId = json.findPath("riderId").intValue();
                rider.startNr = json.findPath("startNr").intValue();
                rider.teamName = json.findPath("teamName").textValue();
                rider.teamShortName = json.findPath("teamShortName").textValue();
                completableFuture.complete(rider);
                return rider;
            } catch (Exception e){
                completableFuture.obtrudeException(e);
                throw  e;
            }
        });

        return completableFuture;
    }


    public CompletionStage<Result> deleteAllRiders(){
        return riderRepository.deleteAllRiders().thenApply(riders -> {
            return ok(riders + " have been deleted.");
        }).exceptionally(ex -> {return internalServerError(ex.getMessage());});
    }

    public CompletionStage<Result> deleteRider(int riderId){
        return riderRepository.deleteRider(riderId).thenApplyAsync(rider -> {
            return ok(rider + " has been deleted");
        }).exceptionally(ex -> {
            Result res = null;
            switch (ExceptionUtils.getRootCause(ex).getClass().getSimpleName()){
                case "NoResultException":
                    res = badRequest("Stage with Id: " + riderId + " not found in DB.");
                    break;
                default:
                    res = internalServerError(ex.getMessage());
            }
            return res;
        });
    }
}
