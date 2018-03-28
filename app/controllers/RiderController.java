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
        return riderRepository.getAllRiders().thenApplyAsync(riders -> ok(toJson(riders.collect(Collectors.toList())))).exceptionally(ex -> {
            Result res;
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

    public CompletionStage<Result> getRider(long riderId){
        return riderRepository.getRider(riderId).thenApplyAsync(rider -> ok(toJson(rider))).exceptionally(ex -> {
            Result res;
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
        return parseRider(json).thenApply(riderRepository::addRider).thenApply(rider -> ok(toJson(rider) + " has been added")).exceptionally(ex -> {
            Result res;
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
                rider.setCountry(json.findPath("country").textValue());
                rider.setUnkown(json.findPath("isUnknown").booleanValue());
                rider.setName(json.findPath("name").textValue());
                rider.setStartNr(json.findPath("startNr").intValue());
                rider.setTeamName(json.findPath("teamName").textValue());
                rider.setTeamShortName(json.findPath("teamShortName").textValue());
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
        return riderRepository.deleteAllRiders().thenApply(riders -> ok(toJson(riders.collect(Collectors.toList())) + " have been deleted.")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    public CompletionStage<Result> deleteRider(long riderId){
        return riderRepository.deleteRider(riderId).thenApplyAsync(rider -> ok(toJson(rider) + " has been deleted")).exceptionally(ex -> {
            Result res;
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
