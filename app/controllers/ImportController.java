package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.JudgmentRiderConnection;
import models.Notification;
import models.RiderStageConnection;
import models.enums.NotificationType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repository.interfaces.*;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static play.libs.Json.toJson;

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

    @Inject
    public ImportController(JudgmentRepository judgmentRepository, JudgmentRiderConnectionRepository judgmentRiderConnectionRepository,
                            MaillotRepository maillotRepository, NotificationRepository notificationRepository,
                            RaceGroupRepository raceGroupRepository, RaceRepository raceRepository,
                            RewardRepository rewardRepository, RiderRankingRepository riderRankingRepository,
                            RiderRepository riderRepository, RiderStageConnectionRepository riderStageConnectionRepository, StageRepository stageRepository) {
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
    }

    public CompletionStage<Result> importAllStaticData() {
        return importRace().thenApply(race -> {
            importStages().thenApply(stage -> {
               importRiders().thenApply(rider -> {
                    importMaillots().thenApply(maillot -> {
                        importJudgments().thenApply(judgment -> {
                            importRewards().thenApply(reward -> {
                                return ok("successfully imported rewards");
                            });
                            return ok("successfully imported judgments");
                        });
                        return ok("successfully imported maillots");
                    });
                   return ok("successfully imported riders");
               }) ;
               return ok("successfully imported stages");
            });
            return ok("successfully imported race");
        })
        .exceptionally(ex -> {
          return internalServerError(ex.getMessage());
        });
    }



    private CompletionStage<Result> importRace(){
        return  null;
    }


    private CompletionStage<Result> importStages(){
        return  null;
    }

    private CompletionStage<Result> importRiders(){
        createRiderStageConnections();
        return null;
    }

    private CompletionStage<Result>  createRiderStageConnections(){
        createDefaultRaceGroup();
        return null;
    }

    private CompletionStage<Result>  createDefaultRaceGroup(){
        return null;
    }

    private CompletionStage<Result> importMaillots(){
        importMaillotRiderConnections();
        return null;
    }

    private CompletionStage<Result> importMaillotRiderConnections(){
        return null;
    }

    private CompletionStage<Result> importJudgments(){
        return  null;
    }

    private CompletionStage<Result> importRewards(){
        return  null;
    }

}
