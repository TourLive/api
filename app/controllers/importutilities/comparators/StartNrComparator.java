package controllers.importutilities.comparators;

import models.RiderStageConnection;

import java.util.Comparator;

public class StartNrComparator implements Comparator<RiderStageConnection> {
    @Override
    public int compare(RiderStageConnection a, RiderStageConnection b) {
        return (int)(a.getRider().getStartNr() - b.getRider().getStartNr());
    }
}
