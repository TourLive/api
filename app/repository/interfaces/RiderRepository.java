package repository.interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;
import models.Rider;
import repository.RiderRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderRepositoryImpl.class)
public interface RiderRepository {
    CompletionStage<JsonNode> getAllRiders();
    CompletionStage<JsonNode> getRider(int riderId);
    CompletionStage<JsonNode> addRider(Rider rider);
    CompletionStage<JsonNode> deleteAllRiders();
    CompletionStage<JsonNode> deleteRider(int riderId);
}
