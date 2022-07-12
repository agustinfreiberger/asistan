package ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;

public class ActivityCondition extends Condition {

    @NotNull
    private final List<Integer> activities;
    private double minConfidence;

    public ActivityCondition(double minConfidence, Integer... activities) {
        this.activities = new ArrayList<>(Arrays.asList(activities));
        this.minConfidence = minConfidence;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return (event.getConfidence() >= this.minConfidence && this.activities.contains(event.getActivity()));
    }

}
