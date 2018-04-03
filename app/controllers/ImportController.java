package controllers;

import akka.http.impl.util.JavaMapping;
import controllers.importUtilities.*;
import models.*;
import models.enums.RaceGroupType;
import play.libs.ws.*;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.*;
import scala.concurrent.ExecutionContextExecutor;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;


public class ImportController extends Controller {
    private final JudgmentRepository judgmentRepository;
    private final JudgmentRiderConnectionRepository judgmentRiderConnectionRepository;
    private final MaillotRepository maillotRepository;
    private final NotificationRepository notificationRepository;
    private final RaceGroupRepository raceGroupRepository;
    private final RaceRepository raceRepository;
    private final RewardRepository rewardRepository;
    private final RiderRankingRepository riderRankingRepository;
    private final RiderRepository riderRepository;
    private final RiderStageConnectionRepository riderStageConnectionRepository;
    private final StageRepository stageRepository;
    private final WSClient wsClient;
    private final ExecutionContextExecutor executionContextExecutor;

    @Inject
    public ImportController(JudgmentRepository judgmentRepository, JudgmentRiderConnectionRepository judgmentRiderConnectionRepository,
                            MaillotRepository maillotRepository, NotificationRepository notificationRepository,
                            RaceGroupRepository raceGroupRepository, RaceRepository raceRepository,
                            RewardRepository rewardRepository, RiderRankingRepository riderRankingRepository,
                            RiderRepository riderRepository, RiderStageConnectionRepository riderStageConnectionRepository,
                            StageRepository stageRepository, WSClient wsClient, ExecutionContextExecutor executionContextExecutor) {
        this.judgmentRepository = judgmentRepository;
        this.judgmentRiderConnectionRepository = judgmentRiderConnectionRepository;
        this.maillotRepository = maillotRepository;
        this.notificationRepository = notificationRepository;
        this.raceGroupRepository = raceGroupRepository;
        this.raceRepository = raceRepository;
        this.rewardRepository = rewardRepository;
        this.riderRankingRepository = riderRankingRepository;
        this.riderRepository = riderRepository;
        this.riderStageConnectionRepository = riderStageConnectionRepository;
        this.stageRepository = stageRepository;
        this.wsClient = wsClient;
        this.executionContextExecutor = executionContextExecutor;
    }

    public CompletionStage<Result> importAllStaticData() {
        deleteAllData();
        return importRace().thenApply(race -> {
            importStages().thenApply(stage -> {
               importRiders().thenApply(rider -> {
                   importRewards().thenApply(judgment -> {
                            importJudgments().thenApply(reward -> {
                                return ok("successfully imported judgments");
                            }).exceptionally(ex -> {
                                return internalServerError("importing judgments failed");
                            });
                            return ok("successfully imported rewards");
                        }) .exceptionally(ex -> {
                            return internalServerError("importing rewards failed");
                        });
                   return ok("successfully imported riders");
               }) .exceptionally(ex -> {
                   return internalServerError("importing riders failed");
               });
               return ok("successfully imported stages");
            }) .exceptionally(ex -> {
                return internalServerError("importing stages failed");
            });
            return ok("successfully imported race");
        }).exceptionally(ex -> {
          return internalServerError("importing race failed");
        });
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
    }

    private CompletionStage<String> importRace(){
        WSRequest request = wsClient.url(UrlLinks.RACE);
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<String> promiseRaceId = request.get().thenApply(res -> {
            return Parser.setActualRaceId(res.asJson());
        });
        promiseRaceId.toCompletableFuture().join();
        long test = UrlLinks.getRaceId();
        request = wsClient.url(UrlLinks.STAGES + UrlLinks.getRaceId());
        CompletionStage<Race> promiseRace = request.get().thenApply(res -> Parser.ParseRace(res.asJson()));
        Race race = promiseRace.toCompletableFuture().join();
        raceRepository.addRace(race);

        request = wsClient.url(UrlLinks.MAILLOTS + UrlLinks.getRaceId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<List<Maillot>> promiseMaillot = request.get().thenApply(res -> Parser.ParseMaillots(res.asJson()));
        List<Maillot> maillots = promiseMaillot.toCompletableFuture().join();
        Race dbRace = CompletableFuture.completedFuture(raceRepository.getRace(UrlLinks.getRaceId())).join().toCompletableFuture().join();
        for(Maillot m : maillots){
            maillotRepository.addMaillot(m);
            Maillot dbMaillot = CompletableFuture.completedFuture(maillotRepository.getMaillot(m.getId())).join().toCompletableFuture().join();
            dbMaillot.setRace(dbRace);
            maillotRepository.updateMaillot(dbMaillot);
        }
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importStages(){
        WSRequest request = wsClient.url(UrlLinks.STAGES + UrlLinks.getRaceId());
        Race race = CompletableFuture.completedFuture(raceRepository.getRace(UrlLinks.getRaceId())).join().toCompletableFuture().join();

        CompletionStage<List<Stage>> promiseStages = request.get().thenApply(res -> Parser.ParseStages(res.asJson()));
        List<Stage> stages = promiseStages.toCompletableFuture().join();
        for(Stage s : stages) {
            stageRepository.addStage(s);
            Stage stage = CompletableFuture.completedFuture(stageRepository.getStage(s.getId())).join().toCompletableFuture().join();
            stage.setRace(race);
            stageRepository.updateStage(stage);
        }
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importMaillots(){

        importMaillotRiderConnections();
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importRiders(){
        List<Stage> stages = CompletableFuture.completedFuture(stageRepository.getAllStages()).join().toCompletableFuture().join().collect(Collectors.toList());
        boolean oneTimeImportRiders = false;
        List<Rider> riders = new ArrayList<>();
        for(Stage stage : stages){
            if(!oneTimeImportRiders){
                WSRequest request = wsClient.url(UrlLinks.RIDERS + stage.getStageId());
                request.setRequestTimeout(java.time.Duration.ofMillis(10000));
                CompletionStage<List<Rider>> promiseRiders = request.get().thenApply(res -> Parser.ParseRiders(res.asJson()));
                riders = promiseRiders.toCompletableFuture().join();
                for(Rider r : riders){
                    riderRepository.addRider(r);
                }
                oneTimeImportRiders = true;
            }
            createRiderStageConnections(stage, riders).toCompletableFuture().join();
            createDefaultRaceGroup(stage).toCompletableFuture().join();
        }

        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String>  createRiderStageConnections(Stage stage, List<Rider> riders){
        WSRequest request = wsClient.url(UrlLinks.RIDERS + stage.getStageId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<List<RiderStageConnection>> promiseRiderStageConnections = request.get().thenApply(res -> Parser.ParseRiderStageConnections(res.asJson()));
        List<RiderStageConnection> riderStageConnections = promiseRiderStageConnections.toCompletableFuture().join();
        for(int i = 0; i < riderStageConnections.size(); i++){
            RiderStageConnection rSC = riderStageConnections.get(i);
            riderStageConnectionRepository.addRiderStageConnection(rSC);
            RiderStageConnection dbRSC = CompletableFuture.completedFuture(riderStageConnectionRepository.getRiderStageConnection(rSC.getId())).join().toCompletableFuture().join();
            Rider dbRider = riderRepository.getRider(riders.get(i).getRiderId());
            Stage dbStage = CompletableFuture.completedFuture(stageRepository.getStage(stage.getId())).join().toCompletableFuture().join();
            dbRSC.setStage(dbStage);
            dbRSC.setRider(dbRider);
            riderStageConnectionRepository.updateRiderStageConnection(dbRSC);
        }
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String>  createDefaultRaceGroup(Stage stage){
        List<Rider> dbRiders = CompletableFuture.completedFuture(riderRepository.getAllRiders()).join();
        Stage dbStage = CompletableFuture.completedFuture(stageRepository.getStage(stage.getId())).join().toCompletableFuture().join();
        RaceGroup raceGroup = new RaceGroup();
        raceGroup.setActualGapTime(0);
        raceGroup.setHistoryGapTime(0);
        raceGroup.setTimestamp(new Timestamp(System.currentTimeMillis()));
        raceGroup.setPosition(0);
        raceGroup.setRaceGroupType(RaceGroupType.FELD);
        raceGroupRepository.addRaceGroup(raceGroup).toCompletableFuture().join();
        RaceGroup dbRaceGroup = CompletableFuture.completedFuture(raceGroupRepository.getRaceGroupById(raceGroup.getId())).join().toCompletableFuture().join();
        dbRaceGroup.setRiders(dbRiders);
        dbRaceGroup.setStage(dbStage);
        raceGroupRepository.updateRaceGroup(dbRaceGroup).toCompletableFuture().join();
        return CompletableFuture.completedFuture("success");
    }



    private CompletionStage<String> importMaillotRiderConnections(){
        // Link Maillot to rider over riderstageconnection, LINK: RIDERJERSEY + STAGEID
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importRewards(){
        WSRequest request = wsClient.url(UrlLinks.JUDGEMENTS + UrlLinks.getRaceId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<List<Reward>> promiseRewards = request.get().thenApply(res -> {
            return Parser.ParseRewards(res.asJson());
        });
        List<Reward> rewards = promiseRewards.toCompletableFuture().join();
        for(Reward r : rewards){
            rewardRepository.addReward(r);
        }
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importJudgments(){
        WSRequest request = wsClient.url(UrlLinks.JUDGEMENTS + UrlLinks.getRaceId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<HashMap<Long, ArrayList<Judgment>>> promiseJudgments = request.get().thenApply(res -> {
            return Parser.ParseJudgments(res.asJson());
        });
        HashMap<Long, ArrayList<Judgment>> judgments = promiseJudgments.toCompletableFuture().join();
        List<Reward> dbRewards = CompletableFuture.completedFuture(rewardRepository.getAllRewards()).join().collect(Collectors.toList());
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
                Judgment dbJ = CompletableFuture.completedFuture(judgmentRepository.getJudgmentById(j.getId())).join();
                dbJ.setReward(dbReward);
                judgmentRepository.updateJudgment(dbJ);
            }
        }
        return CompletableFuture.completedFuture("success");
    }
}
