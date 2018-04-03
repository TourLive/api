package controllers.importUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.*;
import models.enums.RewardType;
import models.enums.StageType;
import models.enums.TypeState;
import scala.Int;

import java.util.*;


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

    public static final Setting ParseSettings(JsonNode json){
        Setting setting = new Setting();
        setting.setStageID(json.findPath("stageID").longValue());
        setting.setRaceID(json.findPath("raceID").longValue());
        return setting;
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
            stage.setStageId(n.findPath("stageId").longValue());
            stages.add(stage);
        }
        return stages;
    }

    public static final List<Maillot> ParseMaillots(JsonNode json){
        ArrayList<Maillot> maillots = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json) {
            Maillot maillot = new Maillot();
            maillot.setColor(json.findPath("color").asText());
            maillot.setPartner(json.findPath("partner").asText());
            maillot.setName(json.findPath("name").textValue());
            maillot.setType(json.findPath("type").textValue());
            maillots.add(maillot);
        }
        return maillots;
    }

    public static final List<Rider> ParseRiders(JsonNode json){
        ArrayList<Rider> riders = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json.findPath("data")) {
            Rider rider = new Rider();
            rider.setCountry(n.findPath("country").textValue());
            rider.setName(n.findPath("name").textValue());
            rider.setRiderId(n.findPath("riderId").longValue());
            rider.setStartNr(n.findPath("startNr").intValue());
            rider.setTeamName(n.findPath("team").textValue());
            rider.setTeamShortName(n.findPath("teamShort").textValue());
            rider.setUnkown(false);
            riders.add(rider);
        }
        return riders;
    }

    public static final List<RiderStageConnection> ParseRiderStageConnections(JsonNode json){
        ArrayList<RiderStageConnection> riderStageConnections = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json.findPath("data")) {
            RiderStageConnection rSC = new RiderStageConnection();
            rSC.setBonusPoints(0);
            rSC.setBonusTime(0);
            rSC.setOfficialGap(json.findPath("timeRueckLong").longValue());
            rSC.setOfficialTime(json.findPath("timeOffLong").longValue());
            rSC.setVirtualGap(json.findPath("timeVirtLong").longValue());
            boolean state = json.findPath("active").booleanValue();
            if (state) {
                rSC.setTypeState(TypeState.ACTIVE);
            } else {
                rSC.setTypeState(TypeState.DNC);
            }
            riderStageConnections.add(rSC);
        }
        return riderStageConnections;
    }

    public static final List<Reward> ParseRewards(JsonNode jsonNode){
        ArrayList<Reward> rewards = new ArrayList<>();
        for (JsonNode n : (ArrayNode) jsonNode.findPath("rewards")) {
            Reward reward = new Reward();

            reward.setRewardId(Long.valueOf(n.path("id").textValue()));

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

    public static final HashMap<Long, ArrayList<Judgment>> ParseJudgments(JsonNode jsonNode){
        HashMap<Long, ArrayList<Judgment>> judgmentHashMap = new HashMap<Long, ArrayList<Judgment>>();
        for (JsonNode n : (ArrayNode) jsonNode.findPath("judgements")) {
            Judgment judgment = new Judgment();
            judgment.setDistance(n.findPath("rennkm").asDouble());
            judgment.setName(n.findPath("name").asText());
            long rewardId = n.findPath("rewardId").asLong();
            if(judgmentHashMap.containsKey(rewardId)){
                judgmentHashMap.get(rewardId).add(judgment);
            } else {
                ArrayList<Judgment> judgments = new ArrayList<>();
                judgments.add(judgment);
                judgmentHashMap.put(rewardId, judgments);
            }
        }
        return judgmentHashMap;
    }
}
