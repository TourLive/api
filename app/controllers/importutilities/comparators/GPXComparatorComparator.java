package controllers.importutilities.comparators;

import models.GPXTrack;

import java.util.Comparator;

public class GPXComparatorComparator implements Comparator<GPXTrack> {
    @Override
    public int compare(GPXTrack a, GPXTrack b) {
        return (int)(a.getId() - b.getId());
    }
}
