package controllers.importUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.Race;
import models.Stage;
import models.enums.StageType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public final class Parser {
    private Parser(){throw new IllegalStateException("Static calss");}

    public static final String setActualRaceId(JsonNode json ){
        UrlLinks.setRaceId(Long.valueOf(json.get(5).findPath("parameter").textValue()));
        return "Actual race id has beent set to " + UrlLinks.getRaceId();
    }

    public static final Race ParseRace(JsonNode json){
        Race race = new Race();
        JsonNode actualRace = json.get(0);
        race.setId(UrlLinks.getRaceId());
        race.setName(actualRace.findPath("race").findPath("raceName").asText());
        return race;
    }

    public static final List<Stage> ParseStages(JsonNode json){
        ArrayList<Stage> stages = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json) {
            Stage stage = new Stage();
            stage.setDestination(n.findPath("to").textValue());
            stage.setStart(n.findPath("from").textValue());
            stage.setDistance(n.findPath("distance").asDouble());
            stage.setEndTime(new Date(n.findPath("endtimeAsTimestamp").asLong()));
            stage.setStartTime(new Date(n.findPath("starttimeAsTimestamp").asLong()));
            stage.setStageType(StageType.valueOf(n.findPath("stagetype").textValue()));
            stage.setId(n.findPath("stageId").longValue());
            stages.add(stage);
        }
        return stages;
    }
}
