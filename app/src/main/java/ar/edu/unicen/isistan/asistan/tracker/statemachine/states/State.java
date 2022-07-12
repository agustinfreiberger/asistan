package ar.edu.unicen.isistan.asistan.tracker.statemachine.states;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import ar.edu.unicen.isistan.asistan.map.DetailedMapContext;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachine;

public abstract class State {

    protected static final long SIX_HOURS = 21600000L;
    protected static final long ONE_DAY = 86400000L;
    protected static final long TWENTY_FIVE_MINUTES = 1500000L;
    protected static final double MAX_DISTANCE = 100.0D;

    private final transient String key;
    protected final transient StateMachine stateMachine;
    protected final transient DetailedMapContext mapContext;

    protected final Event firstEvent;
    protected final Event lastEvent;

    protected State(@NotNull StateMachine stateMachine, @NotNull String key, @NotNull DetailedMapContext mapContext) {
        this.firstEvent = new Event();
        this.lastEvent = new Event();
        this.stateMachine = stateMachine;
        this.key = key;
        this.mapContext = mapContext;
    }

    protected abstract State processEvent(@NotNull Event event);

    public void save(@NotNull SharedPreferences.Editor editor) {
        editor.putString(this.getKey(),new Gson().toJson(this));
    }

    public abstract void load(@NotNull SharedPreferences preferences);

    @Nullable
    public State newEvent(Event event) {
        if (!this.isOpen()) {
            this.mapContext.update(event.getLocation());
            this.add(event);
            return this;
        } else if (this.lastEvent.getTime() < event.getTime()) {
            if (this.isInconsistent(event)) {
                this.abort();
                return null;
            } else {
                this.mapContext.update(event.getLocation());
                return this.processEvent(event);
            }
        } else
            return this;
    }

    protected abstract void abort();

    protected double distance(@NotNull Event event) {
        return event.getLocation().distance(this.lastEvent.getLocation()) - event.getAccuracy() - this.lastEvent.getAccuracy();
    }

    protected boolean isInconsistent(@NotNull Event event) {
        long timeDiff = event.getTime() - this.lastEvent.getTime();

        if (timeDiff > ONE_DAY)
            return true;

        if (this.distance(event) < MAX_DISTANCE)
            return false;

        return timeDiff > inconsistentTime();
    }

    protected abstract long inconsistentTime();

    protected void add(Event event) {
        if (!this.isOpen())
            this.firstEvent.init(event);
        this.lastEvent.init(event);
    }

    protected void addAll(List<Event> events) {
        for (Event event: events)
            this.add(event);
    }

    public boolean isOpen() {
        return !this.firstEvent.isEmpty();
    }

    protected void close() {
        this.lastEvent.init();
        this.firstEvent.init();
    }

    public long startTime() {
        return this.firstEvent.getTime();
    }

    public long endTime() {
        return this.lastEvent.getTime();
    }

    @NotNull
    public Event last() {
        return this.lastEvent;
    }

    @NotNull
    public String getKey() {
        return this.key;
    }

    public abstract void clear();

}
