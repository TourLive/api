package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Rider;
import repository.RiderRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderRepositoryImpl.class)
public interface RiderRepository {
    CompletionStage<Stream<Rider>> getAllRiders();
    CompletionStage<Rider> getRider(int riderId);
    CompletionStage<Rider>  addRider(Rider rider);
    CompletionStage<Stream<Rider>>  deleteAllRiders();
    CompletionStage<Rider>  deleteRider(int riderId);
}
