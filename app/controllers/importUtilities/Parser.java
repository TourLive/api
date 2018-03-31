package controllers.importUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.Race;
import models.Reward;
import models.Stage;
import models.enums.RewardType;
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
            stages.add(stage);
        }
        return stages;
    }

    public static final List<Reward> ParseRewards(JsonNode jsonNode){
        ArrayList<Reward> rewards = new ArrayList<>();
        for (JsonNode n : (ArrayNode) jsonNode.findPath("rewards")) {
            Reward reward = new Reward();

            reward.setId(Long.valueOf(n.path("id").textValue()));

            ArrayList<Integer> moneyList = new ArrayList<>();
            String[] moneyString = n.get("reward").textValue().split(",");
            for (String s : moneyString) {
                moneyList.add(Integer.valueOf(s));
            }
            reward.setMoney(moneyList);

            ArrayList<Integer> pointList = new ArrayList<>();
            String[] pointString = n.get("bonus").textValue().split(",");
            for (String s : pointString) {
                pointList.add(Integer.valueOf(s));
            }
            reward.setPoints(pointList);

            String bonusType = n.findPath("bonusType").textValue();
            if (bonusType.equals("time")) {
                reward.setRewardType(RewardType.TIME);
            }
            if (bonusType.equals("points")) {
                reward.setRewardType(RewardType.POINTS);
            }

            rewards.add(reward);
        }
        return rewards;
    }
}
