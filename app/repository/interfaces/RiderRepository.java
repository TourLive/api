package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Rider;
import repository.RiderRespositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderRespositoryImpl.class)
public interface RiderRepository {
    CompletionStage<Stream<Rider>> getAllRiders();
    void addRider(CompletionStage<Rider> rider);
    CompletionStage<Rider> getRider(int riderId);
    void deleteAllRiders();
    void deleteRider(int riderId);
}
