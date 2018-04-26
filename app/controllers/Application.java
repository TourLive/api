package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.hash.Hashing;
import io.swagger.annotations.*;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.UserRepository;
import views.html.index;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@SwaggerDefinition(
        consumes = {"application/json", "application/xml"},
        produces = {"application/json", "application/xml"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        tags = {
                @Tag(name = "Private", description = "Tag used to denote operations as private")
        }
)
@Api("Application")
public class Application extends Controller {
    private final UserRepository userRepository;
    @Inject
    public Application(UserRepository userRepository) { this.userRepository = userRepository; }

    @ApiOperation(value ="Index Page")
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    @ApiOperation(value ="Swagger Documentation")
    public Result redirectDocs() {
        return  redirect("/assets/lib/swagger-ui/index.html?url=http://localhost:9000/swagger.json");
    }

    @ApiOperation(value ="Status Page", response = String.class)
    public Result statusPage() {
        return ok("{\"status\": \"I'm alive\"}").as("application/json");
    }

    @ApiOperation(value ="Check Login", response = String.class)
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
