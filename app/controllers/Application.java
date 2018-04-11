package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result redirectDocs() {
        return  redirect("/assets/lib/swagger-ui/index.html?url=http://localhost:9000/swagger.json");
    }

    public Result statusPage() {
        return ok("{\"status\": \"I'm alive\"}").as("application/json");
    }
}
