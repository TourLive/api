package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Maillot;
import repository.MaillotRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(MaillotRepositoryImpl.class)
public interface MaillotRepository {
    CompletionStage<Stream<Maillot>> getAllMaillots();
    CompletionStage<Maillot> getMaillot(long maillotId);
    void addMaillot(Maillot maillot);
    void updateMaillot(Maillot maillot);
    void deleteAllMaillots();
    void deleteMaillot(long stageId, long maillotId);
}
