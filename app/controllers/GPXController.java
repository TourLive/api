package controllers;

import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import models.RiderStageConnection;
import models.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.RiderStageConnectionRepository;
import repository.interfaces.StageRepository;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

//@With(BasicAuthAction.class) set active after implementing
public class GPXController extends Controller {
    private final StageRepository stageRepository;
    private static final String ATTRIBUTE_CODE ="code";
    private static final String RANKING = "ranking";
    private static final String RESULTS = "results";
    private static final String CODE_ITG = "ITG";
    private static final String CODE_IPG = "IPG";
    private static final short ALLOWED_TYPE = 1;
    private static final String ATTRIBUTE_NUMBER = "number";
    private static final String ATTRIBUTE_CAPITAL = "capital";
    private static final String ATTRIBUTE_GAP = "gap";

    @Inject
    public GPXController(StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @ApiOperation(value ="import GPS-Coordinates for specific stage", response = Result.class)
    @BodyParser.Of(BodyParser.Xml.class)
    public CompletionStage<Result> addStageGPSTags(long stageId) {
        return updateStageWithXML(stageId, request().body().asXml()).thenApplyAsync(res -> ok("successfully imported GPS Tags for Stage")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    private CompletableFuture<String> updateStageWithXML(long stageId, Document xml) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                Stage stage = stageRepository.getStage(stageId).toCompletableFuture().join();
                xml.getDocumentElement().normalize();

                completableFuture.complete("success");

            } catch (Exception e) {
                completableFuture.obtrudeException(e);
            }
        });

        return completableFuture;
    }
}
