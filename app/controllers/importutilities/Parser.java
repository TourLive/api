package controllers.importutilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.*;
import models.enums.RewardType;
import models.enums.StageType;
import models.enums.TypeState;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public final class Parser {
    private Parser(){throw new IllegalStateException("Static calss");}

    public static final String setActualRaceId(JsonNode json ){
        UrlLinks.setRaceId(Long.valueOf(json.get(5).findPath("parameter").textValue()));
        return "Actual race id has beent set to " + UrlLinks.getRaceId();
    }

    public static final Race parseRace(JsonNode json){
        Race race = new Race();
        JsonNode actualRace = json.get(0);
        race.setId(UrlLinks.getRaceId());
        race.setName(actualRace.findPath("race").findPath("raceName").asText());
        return race;
    }

    public static final Setting parseSettings(JsonNode json){
        Setting setting = new Setting();
        setting.setStageID(json.findPath("stageID").longValue());
        setting.setRaceID(json.findPath("raceID").longValue());
        return setting;
    }

    public static final List<Stage> parseStages(JsonNode json){
        ArrayList<Stage> stages = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json) {
            Stage stage = new Stage();
            stage.setDestination(n.findPath("to").textValue());
            stage.setStart(n.findPath("from").textValue());
            stage.setDistance(n.findPath("distance").asDouble());
            stage.setStageName(n.findPath("stageName").asText());
            stage.setEndTime(new Date(n.findPath("endtimeAsTimestamp").asLong()));
            stage.setStartTime(new Date(n.findPath("starttimeAsTimestamp").asLong()));
            stage.setStageType(StageType.valueOf(n.findPath("stagetype").textValue()));
            stage.setStageId(n.findPath("stageId").longValue());
            stages.add(stage);
        }
        return stages;
    }

    public static final List<Maillot> parseMaillots(JsonNode json){
        ArrayList<Maillot> maillots = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json) {
            Maillot maillot = new Maillot();
            maillot.setColor(n.findPath("color").asText());
            maillot.setPartner(n.findPath("partner").asText());
            maillot.setName(n.findPath("name").textValue());
            maillot.setType(n.findPath("type").textValue());
            maillots.add(maillot);
        }
        return maillots;
    }

    public static final List<Rider> parseRiders(JsonNode json){
        ArrayList<Rider> riders = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json.findPath("data")) {
            Rider rider = new Rider();
            rider.setCountry(n.findPath("country").textValue());
            rider.setName(n.findPath("name").textValue());
            rider.setRiderId(n.findPath("riderId").longValue());
            rider.setStartNr(n.findPath("startNr").intValue());
            rider.setTeamName(n.findPath("team").textValue());
            rider.setTeamShortName(n.findPath("teamShort").textValue());
            rider.setUnknown(false);
            rider.setRaceId(UrlLinks.getRaceId());
            riders.add(rider);
        }
        return riders;
    }

    public static final List<RiderStageConnection> parseRiderStageConnections(JsonNode json){
        ArrayList<RiderStageConnection> riderStageConnections = new ArrayList<>();
        for (JsonNode n : (ArrayNode)json.findPath("data")) {
            RiderStageConnection rSC = new RiderStageConnection();
            rSC.setBonusPoints(0);
            rSC.setBonusTime(0);
            rSC.setOfficialGap(n.findPath("timeRueckLong").longValue());
            rSC.setOfficialTime(n.findPath("timeOffLong").longValue());
            rSC.setVirtualGap(n.findPath("timeVirtLong").longValue());
            boolean state = n.findPath("active").booleanValue();
            if (state) {
                rSC.setTypeState(TypeState.ACTIVE);
            } else {
                rSC.setTypeState(TypeState.DNC);
            }
            riderStageConnections.add(rSC);
        }
        return riderStageConnections;
    }

    public static final List<Reward> parseRewards(JsonNode jsonNode){
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

    public static final HashMap<Long, ArrayList<Judgment>> parseJudgments(JsonNode jsonNode){
        HashMap<Long, ArrayList<Judgment>> judgmentHashMap = new HashMap<>();
        for (JsonNode n : (ArrayNode) jsonNode.findPath("judgements")) {
            Judgment judgment = new Judgment();
            judgment.setDistance(n.findPath("rennkm").asDouble());
            judgment.setName(n.findPath("name").asText());
            judgment.setcnlabStageId(n.findPath("etappe").asLong());
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
