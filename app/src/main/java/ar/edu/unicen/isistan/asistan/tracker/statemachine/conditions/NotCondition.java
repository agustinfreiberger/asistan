package ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;

public class NotCondition extends Condition {

    @NotNull
    private final Condition condition;

    public NotCondition(@NotNull Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return !this.condition.check(event);
    }

    @Override
    @NotNull
    public Condition negate() {
        return condition;
    };

}
