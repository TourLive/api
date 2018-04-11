package controllers.importutilities.comparators;

import models.RiderStageConnection;

import java.util.Comparator;

public class MountainPointsComparator implements Comparator<RiderStageConnection> {
    @Override
    public int compare(RiderStageConnection a, RiderStageConnection b) {
        return (a.getMountainBonusPoints() - b.getMountainBonusPoints());
    }
}
