package controllers;

import io.swagger.annotations.ApiOperation;
import models.GPXTrack;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.GPXTrackRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

public class GPXController extends Controller {
    private final GPXTrackRepository gpxTrackRepository;
    private static final String TRKPT = "trkpt";
    private static final String LATITUDE ="lat";
    private static final String LONGITUDE ="lon";

    @Inject
    public GPXController(GPXTrackRepository gpxTrackRepository) {
        this.gpxTrackRepository = gpxTrackRepository;
    }

    @With(BasicAuthAction.class)
    @ApiOperation(value ="import GPS-Coordinates for specific stage", response = Result.class)
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
                    gpxTracks.add(gpxTrack);
                }
                gpxTrackRepository.addGPXTracksByStageId(stageId, gpxTracks);
                completableFuture.complete("success");

            } catch (Exception e) {
                completableFuture.obtrudeException(e);
            }
        });

        return completableFuture;
    }

    @ApiOperation(value ="get GPS-Coordinates for specific stage", response = Result.class)
    public CompletionStage<Result> getGPSTracksByStageId(long stageId) {
        return gpxTrackRepository.getGPXTracksByStageId(stageId).thenApplyAsync(gpsTracks -> ok(toJson(gpsTracks.collect(Collectors.toList())))).exceptionally(ex -> internalServerError(ex.getMessage()));
    }

    @With(BasicAuthAction.class)
    @ApiOperation(value ="delete GPS-Coordinates for specific stage", response = Result.class)
    public CompletionStage<Result> deleteGPSTracksForStageById(long stageId) {
        return gpxTrackRepository.deleteGPXTracksByStageId(stageId).thenApplyAsync(res -> ok("successfully deleted all GPX Tracks of specific stage")).exceptionally(ex -> internalServerError(ex.getMessage()));
    }


}
