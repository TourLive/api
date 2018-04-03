package controllers.importUtilities.comparators;

import models.RiderStageConnection;

import java.util.Comparator;

public class PointsComparator implements Comparator<RiderStageConnection> {
    @Override
    public int compare(RiderStageConnection a, RiderStageConnection b) {
        return (int)((a.getBonusPoints() + a.getSprintBonusPoints() + a.getMountainBonusPoints()) -
                (b.getBonusPoints() + b.getSprintBonusPoints() + b.getMountainBonusPoints()));
    }
}
