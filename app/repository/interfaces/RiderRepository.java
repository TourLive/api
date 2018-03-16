package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Rider;
import repository.RiderRespositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(RiderRespositoryImpl.class)
public interface RiderRepository {
    CompletionStage<Stream<Rider>> list();
}
