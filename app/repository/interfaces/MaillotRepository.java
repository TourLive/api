package repository.interfaces;

import models.Maillot;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public interface MaillotRepository {
    CompletionStage<Stream<Maillot>> getAllMaillots();
    void addMaillot(CompletionStage<Maillot> maillot);
    CompletionStage<Maillot> getMaillot(int maillotId);
    void deleteAllMaillots();
    void deleteMaillot(int maillotId);
}
