package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.hash.Hashing;
import io.swagger.annotations.ApiOperation;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.UserRepository;
import views.html.index;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

public class Application extends Controller {
    private final UserRepository userRepository;

    @Inject
    public Application(UserRepository userRepository) { this.userRepository = userRepository; }

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result redirectDocs() {
        return  redirect("/assets/lib/swagger-ui/index.html?url=http://localhost:9000/swagger.json");
    }

    public Result statusPage() {
        return ok("{\"status\": \"I'm alive\"}").as("application/json");
    }

    @ApiOperation(value ="login")
    @BodyParser.Of(BodyParser.Json.class)
    public Result login(){
        JsonNode jsonNode = request().body().asJson();
        String username = jsonNode.findPath("username").asText();
        String password = jsonNode.findPath("password").asText();
        try{
            userRepository.getUser(username, Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
            return ok("login successfull");
        } catch (Exception ex){
            return badRequest(ex.getMessage());
        }
    }
}
