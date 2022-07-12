package ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;

public abstract class Condition {

    public abstract boolean check(@NotNull Event event);

    @NotNull
    public Condition negate() {
        return new NotCondition(this);
    };

}
