package ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.states.State;

public class TimeCondition extends Condition {

    @NotNull
    private final State state;
    private long minElapsedTime;

    public TimeCondition(long minElapsedTime, @NotNull State state) {
        this.minElapsedTime = minElapsedTime;
        this.state = state;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return (event.getTime() - this.state.startTime() >= this.minElapsedTime);
    }

}
