package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.Maillot;
import repository.MaillotRepositoryImpl;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(MaillotRepositoryImpl.class)
public interface MaillotRepository {
    CompletionStage<Stream<Maillot>> getAllMaillots();
    CompletionStage<Maillot> getMaillot(int maillotId);
    void addMaillot(CompletionStage<Maillot> maillot);
    void deleteAllMaillots();
    void deleteMaillot(int maillotId);
}
