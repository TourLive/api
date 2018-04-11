package controllers.importUtilities.comparators;

import models.RiderStageConnection;

import java.util.Comparator;

public class LeaderComparator implements Comparator<RiderStageConnection> {
    @Override
    public int compare(RiderStageConnection a, RiderStageConnection b) {
        return (int)(a.getOfficialGap() - b.getOfficialGap());
    }
}
