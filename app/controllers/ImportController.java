package controllers;

import controllers.importUtilities.*;
import models.Race;
import play.libs.ws.*;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.*;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import javax.swing.plaf.UIResource;
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
        CompletionStage<Race> promiseRace = request.get().thenApply(res -> {
            return Parser.ParseRace(res.asJson());
        });
        Race race = promiseRace.toCompletableFuture().join();
        UrlLinks.setRaceId(race.getId());
        raceRepository.addRace(race);
        return CompletableFuture.completedFuture("success");
    }

    private CompletionStage<String> importStages(){
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
