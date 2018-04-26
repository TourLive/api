package controllers;

import io.swagger.annotations.Api;
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

@Api("Updates")
@With(BasicAuthAction.class)
public class UpdateController extends Controller {
    private final StageRepository stageRepository;
    private final RiderStageConnectionRepository riderStageConnectionRepository;
    private static final String ATTRIBUTE_CODE ="code";
    private static final String RANKING = "ranking";
    private static final String RESULTS = "results";
    private static final String CODE_ITG = "ITG";
    private static final String CODE_IPG = "IPG";
    private static final short ALLOWED_TYPE = 1;
    private static final String ATTRIBUTE_NUMBER = "number";
    private static final String ATTRIBUTE_CAPITAL = "capital";
    private static final String ATTRIBUTE_GAP = "gap";
    private boolean nextStageAvailable = false;

    @Inject
    public UpdateController(StageRepository stageRepository, RiderStageConnectionRepository riderStageConnectionRepository) {
        this.stageRepository = stageRepository;
        this.riderStageConnectionRepository = riderStageConnectionRepository;
    }

    @ApiOperation(value ="update actual and next stage by specific matsport-xml", response = String.class)
    @BodyParser.Of(BodyParser.Xml.class)
    public CompletionStage<Result> updateStage(long stageId) {
        nextStageAvailable = false;
        List<Stage> stages = stageRepository.getAllStagesByRaceId(stageRepository.getStage(stageId).toCompletableFuture().join().getRace().getId()).toCompletableFuture().join().collect(Collectors.toList());
        for(Stage s : stages){
            if(s.getId() == (stageId+1)){
                nextStageAvailable = true;
                break;
            }
        }
        return updateStageWithXML(stageId, request().body().asXml()).thenApplyAsync(value -> ok("successfully updated stages")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    private CompletableFuture<String> updateStageWithXML(long stageId, Document xml) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                xml.getDocumentElement().normalize();
                NodeList rankings = xml.getElementsByTagName(RANKING);
                NodeList results = null;
                for (int i = 0; i < rankings.getLength(); i++) {
                    Node ranking = rankings.item(i);
                    NamedNodeMap attributes = ranking.getAttributes();
                    Node code = attributes.getNamedItem(ATTRIBUTE_CODE);
                    switch (code.getNodeValue()){
                        case CODE_ITG:
                            results = collectResultChildNodes(ranking);
                            updateTimes(stageId, results);
                            if(nextStageAvailable) updateTimes(stageId + 1, results);
                            break;
                        case CODE_IPG:
                            results = collectResultChildNodes(ranking);
                            updatePoints(stageId, results);
                            if(nextStageAvailable) updatePoints(stageId + 1, results);
                            break;
                        default:
                            break;
                    }
                }
                completableFuture.complete("success");

            } catch (Exception e) {
                completableFuture.obtrudeException(e);
            }
        });

        return completableFuture;
    }

    private NodeList collectResultChildNodes(Node ranking) throws NotFoundException {
        NodeList children = ranking.getChildNodes();
        NodeList results = null;
        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            if(child.getNodeType() != ALLOWED_TYPE) continue;
            if(child.getLocalName().equals(RESULTS)){
               results = child.getChildNodes();
               break;
            }
        }
        if(results == null) throw new NotFoundException("failed to find results");
        return results;
    }

    private void updateTimes(long stageId, NodeList results) throws ParseException {
        try{
            for(int i = 0; i < results.getLength(); i++){
                Node result = results.item(i);
                if(result.getNodeType() != ALLOWED_TYPE) continue;
                NamedNodeMap attributes = result.getAttributes();
                int startNr = Integer.parseInt(attributes.getNamedItem(ATTRIBUTE_NUMBER).getNodeValue());
                long officialTime = convertTimeStringToLongInSeconds(attributes.getNamedItem(ATTRIBUTE_CAPITAL).getNodeValue());
                long officialGap = convertTimeStringToLongInSeconds(attributes.getNamedItem(ATTRIBUTE_GAP).getNodeValue());
                RiderStageConnection rSC = riderStageConnectionRepository.getRiderStageConnectionByRiderStartNrAndStage(stageId, startNr).toCompletableFuture().join();
                rSC.setOfficialTime(officialTime);
                rSC.setOfficialGap(officialGap);
                riderStageConnectionRepository.updateRiderStageConnection(rSC).toCompletableFuture().join();
            }
        } catch (Exception ex){
            throw new ParseException("Failed to parse the xml", 0);
        }
    }

    private void updatePoints(long stageId, NodeList results) throws ParseException {
        try{
            for(int i = 0; i < results.getLength(); i++){
                Node result = results.item(i);
                if(result.getNodeType() != ALLOWED_TYPE) continue;
                NamedNodeMap attributes = result.getAttributes();
                int startNr = Integer.parseInt(attributes.getNamedItem(ATTRIBUTE_NUMBER).getNodeValue());
                int bonusPoints = Integer.parseInt(attributes.getNamedItem(ATTRIBUTE_CAPITAL).getNodeValue());
                RiderStageConnection rSC = riderStageConnectionRepository.getRiderStageConnectionByRiderStartNrAndStage(stageId, startNr).toCompletableFuture().join();
                rSC.setBonusPoints(bonusPoints);
                riderStageConnectionRepository.updateRiderStageConnection(rSC).toCompletableFuture().join();
            }
        } catch (Exception ex){
            throw new ParseException("Failed to parse the xml", 0);
        }
    }

    private long convertTimeStringToLongInSeconds(String timeString){
        String[] tokens = timeString.split(":");
        int hours = Integer.parseInt(tokens[0]);
        int minutes = Integer.parseInt(tokens[1]);
        int seconds = Integer.parseInt(tokens[2]);
        return (3600 * hours + 60 * minutes + seconds);
    }
}
