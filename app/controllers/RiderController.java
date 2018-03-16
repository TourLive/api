package controllers;

import play.core.j.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.RiderRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class RiderController extends Controller {
    private final RiderRepository riderRepository;

    @Inject
    public RiderController(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public Result index() {
        return ok("RIDER REPO");
    }

    public CompletionStage<Result> getRiders() {
        return riderRepository.list().thenApplyAsync(riderStream -> {
           return ok(toJson(riderStream.collect(Collectors.toList())));
        });
    }
}
