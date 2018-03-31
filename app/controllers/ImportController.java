package controllers;

import controllers.importUtilities.*;
import models.Race;
import models.Stage;
import play.libs.ws.*;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.*;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.swing.plaf.UIResource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;



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
        return importRace().thenApply(race -> {
            importStages().thenApply(stage -> {
               importRiders().thenApply(rider -> {
                    importMaillots().thenApply(maillot -> {
                        importJudgments().thenApply(judgment -> {
                            importRewards().thenApply(reward -> {
                                return ok("successfully imported rewards");
                            }).exceptionally(ex -> {
                                return internalServerError("importing rewards failed");
                            });
                            return ok("successfully imported judgments");
                        }) .exceptionally(ex -> {
                            return internalServerError("importing judgments failed");
                        });
                        return ok("successfully imported maillots");
                    }) .exceptionally(ex -> {
                        return internalServerError("importing maillots failed");
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

    private CompletionStage<String> importRace(){
        WSRequest request = wsClient.url(UrlLinks.RACE);
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        CompletionStage<String> promiseRaceId = request.get().thenApply(res -> {
            return Parser.setActualRaceId(res.asJson());
        });
        promiseRaceId.toCompletableFuture().join();
        long test = UrlLinks.getRaceId();
        request = wsClient.url(UrlLinks.STAGES + UrlLinks.getRaceId());
        CompletionStage<Race> promiseRace = request.get().thenApply(res -> {
            return Parser.ParseRace(res.asJson());
        });
        Race race = promiseRace.toCompletableFuture().join();
        raceRepository.addRace(race);
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importStages(){
        WSRequest request = wsClient.url(UrlLinks.STAGES + UrlLinks.getRaceId());
        request.setRequestTimeout(java.time.Duration.ofMillis(10000));
        Race race = CompletableFuture.completedFuture(raceRepository.getRace(UrlLinks.getRaceId())).join().toCompletableFuture().join();
        CompletionStage<List<Stage>> promiseStages = request.get().thenApply(res -> {
            return Parser.ParseStages(res.asJson());
        });
        List<Stage> stages = promiseStages.toCompletableFuture().join();
        for(Stage s : stages){

            stageRepository.addStage(s);
            race.getStages().add(s);

        }
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importRiders(){
        createRiderStageConnections();
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String>  createRiderStageConnections(){
        createDefaultRaceGroup();
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String>  createDefaultRaceGroup(){
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importMaillots(){
        importMaillotRiderConnections();
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importMaillotRiderConnections(){
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importJudgments(){
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importRewards(){
        return CompletableFuture.completedFuture("success");
    }
}
