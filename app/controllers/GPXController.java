package controllers;

import controllers.importutilities.comparators.GPXComparatorComparator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.NotFoundException;
import models.GPXTrack;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.cache.AsyncCacheApi;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.GPXTrackRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

@Api("GPX Tracks")
public class GPXController extends Controller {
    private final GPXTrackRepository gpxTrackRepository;
    private static final String TRKPT = "trkpt";
    private static final String LATITUDE ="lat";
    private static final String LONGITUDE ="lon";
    private static final String HEIGHT = "ele";
    private static final short ALLOWED_TYPE = 1;
    private final AsyncCacheApi cache;

    @Inject
    public GPXController(GPXTrackRepository gpxTrackRepository, AsyncCacheApi cache) {
        this.gpxTrackRepository = gpxTrackRepository;
        this.cache = cache;
    }

    @With(BasicAuthAction.class)
    @ApiOperation(value ="Import GPX-Coordinates for specific stage", response = Result.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error on importing/adding gpx tracks") })
    @BodyParser.Of(BodyParser.Xml.class)
    public CompletionStage<Result> addGPSTracksForStageById(long stageId) {
        return updateGPXTracksWithXML(stageId, request().body().asXml()).thenApplyAsync(res -> ok("successfully imported GPS Tracks for Stage")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    private CompletableFuture<String> updateGPXTracksWithXML(long stageId, Document xml) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                ArrayList<GPXTrack> gpxTracks = new ArrayList<>();
                xml.getDocumentElement().normalize();
                NodeList trackPoints = xml.getElementsByTagName(TRKPT);
                for (int i = 0; i < trackPoints.getLength(); i++) {
                    Node trkpt = trackPoints.item(i);
                    NamedNodeMap attributes = trkpt.getAttributes();
                    GPXTrack gpxTrack = new GPXTrack();
                    gpxTrack.setLatitude(Double.valueOf(attributes.getNamedItem(LATITUDE).getNodeValue()));
                    gpxTrack.setLongitude(Double.valueOf(attributes.getNamedItem(LONGITUDE).getNodeValue()));
                    Node height = collectHeight(trkpt);
                    gpxTrack.setHeight(Double.valueOf(height.getNodeValue()));
                    gpxTracks.add(gpxTrack);
                }
                gpxTrackRepository.addGPXTracksByStageId(stageId, gpxTracks);
                completableFuture.complete("success");
                cache.remove("/gpxtracks/stages" + stageId);

            } catch (Exception e) {
                completableFuture.obtrudeException(e);
            }
        });

        return completableFuture;
    }

    private Node collectHeight(Node parent) throws NotFoundException {
        NodeList children = parent.getChildNodes();
        Node result = null;
        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            if(child.getNodeType() != ALLOWED_TYPE) continue;
            if(child.getLocalName().equals(HEIGHT)){
                result = child.getFirstChild();
                break;
            }
        }
        if(result == null) throw new NotFoundException("failed to find results");
        return result;
    }

    @ApiOperation(value ="get GPS-Coordinates for specific stage", response = Result.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "No gpx tracks of specified stage id found") })
    public CompletionStage<Result> getGPSTracksByStageId(long stageId) {
        return cache.getOrElseUpdate("/gpxtracks/stages" + stageId, () -> gpxTrackRepository.getGPXTracksByStageId(stageId).thenApplyAsync(gpsTracks -> {
            List<GPXTrack> sortedGPXTracks = gpsTracks.collect(Collectors.toList());
            sortedGPXTracks.sort(new GPXComparatorComparator());
            return ok(toJson(sortedGPXTracks));
        }).exceptionally(ex -> internalServerError(ex.getMessage())), GlobalConstants.LONG_CACHE_DURATION);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "failed deleting gpx tracks for specified stage id") })
    @With(BasicAuthAction.class)
    @ApiOperation(value ="delete GPS-Coordinates for specific stage", response = Result.class)
    public CompletionStage<Result> deleteGPSTracksForStageById(long stageId) {
        cache.remove("/gpxtracks/stages" + stageId);
        return gpxTrackRepository.deleteGPXTracksByStageId(stageId).thenApplyAsync(res -> ok("successfully deleted all GPX Tracks of specific stage")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }


}
