package controllers.importUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import models.Race;



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
}
