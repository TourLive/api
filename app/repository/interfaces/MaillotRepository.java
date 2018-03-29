package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Maillot;
import repository.MaillotRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(MaillotRepositoryImpl.class)
public interface MaillotRepository {
    CompletionStage<Stream<Maillot>> getAllMaillots(long stageId);
    CompletionStage<Maillot> getMaillot(long stageId, long maillotId);
    void addMaillot(Maillot maillot);
    void deleteAllMaillots();
    void deleteMaillot(long stageId, long maillotId);
}
