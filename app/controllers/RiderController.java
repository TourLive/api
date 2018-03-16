package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import repository.RiderRepository;

import javax.inject.Inject;

public class RiderController extends Controller {
    private final RiderRepository riderRepository;

    @Inject
    public RiderController(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public Result index() {
        return ok("RIDER REPO");
    }
}
