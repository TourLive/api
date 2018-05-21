package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.hash.Hashing;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import play.cache.AsyncCacheApi;
import play.mvc.*;
import repository.interfaces.UserRepository;
import views.html.index;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionStage;

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
    private final AsyncCacheApi cache;

    @Inject
    public Application(UserRepository userRepository, AsyncCacheApi cache) {
        this.userRepository = userRepository;
        this.cache = cache;
    }

    @ApiOperation(value ="Index Page")
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    @ApiOperation(value ="Swagger Documentation")
    public Result redirectDocs() {
        String hostname = request().host();
        return  redirect("/assets/lib/swagger-ui/index.html?url=http://" + hostname + "/swagger.json");
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

    @With(BasicAuthAction.class)
    @ApiOperation(value ="deletes all caches of the application", response = Result.class)
    public CompletionStage<Result> deleteCache() {
        return cache.removeAll().thenApplyAsync(something -> ok("Succesfully deleted caches")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }
}
