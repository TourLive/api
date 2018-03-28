package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Rider;
import repository.RiderRepositoryImpl;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderRepositoryImpl.class)
public interface RiderRepository {
    List<Rider> getAllRiders();
    Rider getRider(long riderId);
    CompletionStage<Stream<Rider>>  getAllRiders(long stageId);
    CompletionStage<Rider> getRiderAsync(long riderId);
    void addRider(Rider rider);
    void deleteAllRiders();
    void deleteRider(long riderId);
}
