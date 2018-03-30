package controllers.importUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import models.Race;


public final class Parser {
    private Parser(){throw new IllegalStateException("Static calss");}

    public static final Race ParseRace(JsonNode json){
        Race race = new Race();
        JsonNode actualRace = json.get(5);
        race.setId(actualRace.findPath("settingsId").asLong());
        race.setName(actualRace.findPath("settingname").asText());
        return race;
    }
}
