package controllers;

import controllers.importutilities.Parser;
import controllers.importutilities.UrlLinks;
import controllers.importutilities.comparators.LeaderComparator;
import controllers.importutilities.comparators.MountainPointsComparator;
import controllers.importutilities.comparators.PointsComparator;
import models.*;
import models.enums.RaceGroupType;
import models.enums.TypeState;
import play.cache.AsyncCacheApi;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import repository.interfaces.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@With(BasicAuthAction.class)
public class ImportController extends Controller {
    private final JudgmentRepository judgmentRepository;
    private final MaillotRepository maillotRepository;
    private final RaceGroupRepository raceGroupRepository;
    private final RaceRepository raceRepository;
    private final RewardRepository rewardRepository;
    private final RiderRepository riderRepository;
    private final RiderStageConnectionRepository riderStageConnectionRepository;
    private final StageRepository stageRepository;
    private final WSClient wsClient;
    private static final String SUCESSMESSAGE ="success";
    private final AsyncCacheApi cache;

    @Inject
    public ImportController(JudgmentRepository judgmentRepository, MaillotRepository maillotRepository,
                            RaceGroupRepository raceGroupRepository, RaceRepository raceRepository,
                            RewardRepository rewardRepository, RiderRepository riderRepository,
                            RiderStageConnectionRepository riderStageConnectionRepository,
                            StageRepository stageRepository, WSClient wsClient, AsyncCacheApi cache) {
        this.judgmentRepository = judgmentRepository;
        this.maillotRepository = maillotRepository;
        this.raceGroupRepository = raceGroupRepository;
        this.raceRepository = raceRepository;
        this.rewardRepository = rewardRepository;
        this.riderRepository = riderRepository;
        this.riderStageConnectionRepository = riderStageConnectionRepository;
        this.stageRepository = stageRepository;
        this.wsClient = wsClient;
        this.cache = cache;
    }

    public CompletionStage<Result> importAllStaticData() {
        deleteAllData();
        return importRace().thenApply(race -> {
            importStages().thenApply(stage -> {
                importMaillots().thenApply(maillot -> {
                    importRiders().thenApply(rider -> {
                        importRewards().thenApply(judgment -> {
                            importJudgments().thenApply(reward -> ok("successfully imported judgments")).exceptionally(ex -> internalServerError("importing judgments failed"));
                            return ok("successfully imported rewards");
                        }) .exceptionally(ex -> internalServerError("importing rewards failed"));
                        return ok("successfully imported riders");
                    }) .exceptionally(ex -> internalServerError("importing riders failed"));
                    return ok("successfully imported maillots");
                }).exceptionally(ex -> internalServerError("importing maillots failed"));
               return ok("successfully imported stages");
            }) .exceptionally(ex -> internalServerError("importing stages failed"));
            return ok("successfully imported race");
        }).exceptionally(ex -> internalServerError("importing race failed"));
    }

    private void deleteAllData(){
        judgmentRepository.deleteAllJudgment();
        rewardRepository.deleteAllRewards();
        maillotRepository.deleteAllMaillots();
        raceGroupRepository.deleteAllRaceGroups();
        riderRepository.deleteAllRiders();
        riderStageConnectionRepository.deleteAllRiderStageConnections();
        stageRepository.deleteAllStages();
        raceRepository.deleteAllRaces();
        cache.removeAll();
    }

    private CompletionStage<String> importRace(){
        WSRequest request = wsClient.url(UrlLinks.RACE);
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<String> promiseRaceId = request.get().thenApply(res -> Parser.setActualRaceId(res.asJson()));
        promiseRaceId.toCompletableFuture().join();
        request = wsClient.url(UrlLinks.STAGES + UrlLinks.getRaceId());
        CompletionStage<Race> promiseRace = request.get().thenApply(res -> Parser.parseRace(res.asJson()));
        Race race = promiseRace.toCompletableFuture().join();
        raceRepository.addRace(race);
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }

    private CompletionStage<String> importStages(){
        WSRequest request = wsClient.url(UrlLinks.STAGES + UrlLinks.getRaceId());
        Race race = CompletableFuture.completedFuture(raceRepository.getRace(UrlLinks.getRaceId())).join().toCompletableFuture().join();

        CompletionStage<List<Stage>> promiseStages = request.get().thenApply(res -> Parser.parseStages(res.asJson()));
        List<Stage> stages = promiseStages.toCompletableFuture().join();
        for(Stage s : stages) {
            stageRepository.addStage(s);
            Stage stage = CompletableFuture.completedFuture(stageRepository.getStage(s.getId())).join().toCompletableFuture().join();
            stage.setRace(race);
            stageRepository.updateStage(stage);
        }
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }

    private CompletionStage<String> importMaillots(){
        WSRequest request = wsClient.url(UrlLinks.MAILLOTS + UrlLinks.getRaceId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        List<Stage> stages = CompletableFuture.completedFuture(stageRepository.getAllStagesByRaceId(UrlLinks.getRaceId())).join().toCompletableFuture().join().collect(Collectors.toList());
        for(Stage s : stages){
            CompletionStage<List<Maillot>> promiseMaillot = request.get().thenApply(res -> Parser.parseMaillots(res.asJson()));
            List<Maillot> test = promiseMaillot.toCompletableFuture().join();
            for(Maillot m : test){
                maillotRepository.addMaillot(m);
                Maillot dbMaillot = CompletableFuture.completedFuture(maillotRepository.getMaillot(m.getId())).join().toCompletableFuture().join();
                dbMaillot.setStage(s);
                maillotRepository.updateMaillot(dbMaillot);
            }
        }
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }

    private CompletionStage<String> importRiders(){
        List<Stage> stages = CompletableFuture.completedFuture(stageRepository.getAllStages()).join().toCompletableFuture().join().collect(Collectors.toList());
        boolean oneTimeImportRiders = false;
        List<Rider> riders = new ArrayList<>();
        for(Stage stage : stages){
            if(!oneTimeImportRiders){
                WSRequest request = wsClient.url(UrlLinks.RIDERS + stage.getStageId());
                request.setRequestTimeout(java.time.Duration.ofMillis(10000));
                CompletionStage<List<Rider>> promiseRiders = request.get().thenApply(res -> Parser.parseRiders(res.asJson()));
                riders = promiseRiders.toCompletableFuture().join();
                for(Rider r : riders){
                    riderRepository.addRider(r);
                }
                oneTimeImportRiders = true;
            }
            createRiderStageConnections(stage, riders).toCompletableFuture().join();
            createMaillotRiderConnections(stage).toCompletableFuture().join();
            createDefaultRaceGroup(stage).toCompletableFuture().join();
        }

        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }

    private CompletionStage<String>  createRiderStageConnections(Stage stage, List<Rider> riders){
        WSRequest request = wsClient.url(UrlLinks.RIDERS + stage.getStageId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<List<RiderStageConnection>> promiseRiderStageConnections = request.get().thenApply(res -> Parser.parseRiderStageConnections(res.asJson()));
        List<RiderStageConnection> riderStageConnections = promiseRiderStageConnections.toCompletableFuture().join();
        for(int i = 0; i < riderStageConnections.size(); i++){
            RiderStageConnection rSC = riderStageConnections.get(i);
            riderStageConnectionRepository.addRiderStageConnection(rSC);
            RiderStageConnection dbRSC = CompletableFuture.completedFuture(riderStageConnectionRepository.getRiderStageConnection(rSC.getId())).join().toCompletableFuture().join();
            Rider dbRider = riderRepository.getRiderByCnlabId(riders.get(i).getRiderId());
            Stage dbStage = CompletableFuture.completedFuture(stageRepository.getStage(stage.getId())).join().toCompletableFuture().join();
            dbRSC.setStage(dbStage);
            dbRSC.setRider(dbRider);
            riderStageConnectionRepository.updateRiderStageConnection(dbRSC);
        }
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }

    private CompletionStage<String> createMaillotRiderConnections(Stage stage){
        List<RiderStageConnection> rSCs = CompletableFuture.completedFuture(riderStageConnectionRepository.getRiderStageConnectionsByStageWithRiderMaillots(stage.getId())).join().toCompletableFuture().join().collect(Collectors.toList());
        List<RiderStageConnection> rSCsBestSwiss = CompletableFuture.completedFuture(riderStageConnectionRepository.getRiderStageConnectionsByStageWithRiderMaillots(stage.getId())).join().toCompletableFuture().join().collect(Collectors.toList());
        rSCsBestSwiss.removeIf(rSC -> !rSC.getRider().getCountry().equals("SUI"));

        List<Maillot> maillots = CompletableFuture.completedFuture(maillotRepository.getAllMaillots(stage.getId())).join().toCompletableFuture().join().collect(Collectors.toList());
        RiderStageConnection leader = null;
        for(Maillot m : maillots){
            switch (m.getType()){
                case "points":
                    Collections.sort(rSCs, new PointsComparator());
                    leader = rSCs.get(0);
                    leader.addRiderMaillots(m);
                    riderStageConnectionRepository.updateRiderStageConnection(leader).toCompletableFuture().join();
                    rSCs.set(0, riderStageConnectionRepository.getRiderStageConnectionByRiderStageConnectionWithRiderMaillots(leader.getId()).toCompletableFuture().join());
                    break;
                case "bestSwiss":
                    Collections.sort(rSCsBestSwiss, new PointsComparator());
                    leader = rSCsBestSwiss.get(0);
                    leader.addRiderMaillots(m);
                    riderStageConnectionRepository.updateRiderStageConnection(leader).toCompletableFuture().join();
                    rSCsBestSwiss.set(0, riderStageConnectionRepository.getRiderStageConnectionByRiderStageConnectionWithRiderMaillots(leader.getId()).toCompletableFuture().join());
                    break;
                case "leader":
                    Collections.sort(rSCs, new LeaderComparator());
                    leader = rSCs.get(0);
                    leader.addRiderMaillots(m);
                    riderStageConnectionRepository.updateRiderStageConnection(leader).toCompletableFuture().join();
                    rSCs.set(0, riderStageConnectionRepository.getRiderStageConnectionByRiderStageConnectionWithRiderMaillots(leader.getId()).toCompletableFuture().join());
                    break;
                case "mountain":
                    Collections.sort(rSCs, new MountainPointsComparator());
                    leader = rSCs.get(0);
                    leader.addRiderMaillots(m);
                    riderStageConnectionRepository.updateRiderStageConnection(leader).toCompletableFuture().join();
                    rSCs.set(0, riderStageConnectionRepository.getRiderStageConnectionByRiderStageConnectionWithRiderMaillots(leader.getId()).toCompletableFuture().join());
                    break;
                default:
                    break;
            }
        }
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }


    private CompletionStage<String>  createDefaultRaceGroup(Stage stage){
        Stream<RiderStageConnection> dbRSC = CompletableFuture.completedFuture(riderStageConnectionRepository.getAllRiderStageConnections(stage.getId())).join().toCompletableFuture().join();
        List<RiderStageConnection> activeRSC = dbRSC.filter(rSC -> rSC.getTypeState() == TypeState.ACTIVE).collect(Collectors.toList());
        List<Rider> activeRiders = new ArrayList<>();
        for(RiderStageConnection s : activeRSC){
            activeRiders.add(s.getRider());
        }
        Stage dbStage = CompletableFuture.completedFuture(stageRepository.getStage(stage.getId())).join().toCompletableFuture().join();
        RaceGroup raceGroup = new RaceGroup();
        raceGroup.setActualGapTime(0);
        raceGroup.setHistoryGapTime(0);
        raceGroup.setPosition(1);
        raceGroup.setRaceGroupType(RaceGroupType.FELD);
        raceGroupRepository.addRaceGroup(raceGroup, System.currentTimeMillis()).toCompletableFuture().join();
        RaceGroup dbRaceGroup = CompletableFuture.completedFuture(raceGroupRepository.getRaceGroupById(raceGroup.getId())).join().toCompletableFuture().join();
        dbRaceGroup.setRiders(activeRiders);
        dbRaceGroup.setStage(dbStage);
        raceGroupRepository.updateRaceGroup(dbRaceGroup, System.currentTimeMillis()).toCompletableFuture().join();
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }

    private CompletionStage<String> importRewards(){
        WSRequest request = wsClient.url(UrlLinks.JUDGEMENTS + UrlLinks.getRaceId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<List<Reward>> promiseRewards = request.get().thenApply(res -> Parser.parseRewards(res.asJson()));
        List<Reward> rewards = promiseRewards.toCompletableFuture().join();
        for(Reward r : rewards){
            rewardRepository.addReward(r);
        }
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }

    private CompletionStage<String> importJudgments(){
        WSRequest request = wsClient.url(UrlLinks.JUDGEMENTS + UrlLinks.getRaceId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<HashMap<Long, ArrayList<Judgment>>> promiseJudgments = request.get().thenApply(res -> Parser.parseJudgments(res.asJson()));
        HashMap<Long, ArrayList<Judgment>> judgments = promiseJudgments.toCompletableFuture().join();
        List<Reward> dbRewards = CompletableFuture.completedFuture(rewardRepository.getAllRewards()).join().toCompletableFuture().join().collect(Collectors.toList());

        for(Long rewardId : judgments.keySet()){
            Reward dbReward = null;
            for(Reward r : dbRewards){
                if(r.getRewardId() == rewardId){
                    dbReward = r;
                    break;
                }
            }
            ArrayList<Judgment> judgmentList = judgments.get(rewardId);
            for (Judgment j : judgmentList) {
                judgmentRepository.addJudgment(j);
                Judgment dbJ = CompletableFuture.completedFuture(judgmentRepository.getJudgmentByIdCompleted(j.getId())).join().toCompletableFuture().join();
                dbJ.setReward(dbReward);
                Stage stage = stageRepository.getStageByCnlabId(dbJ.getcnlabStageId()).toCompletableFuture().join();
                dbJ.setStage(stage);
                judgmentRepository.updateJudgment(dbJ);
            }
        }
        return CompletableFuture.completedFuture(SUCESSMESSAGE);
    }
}

