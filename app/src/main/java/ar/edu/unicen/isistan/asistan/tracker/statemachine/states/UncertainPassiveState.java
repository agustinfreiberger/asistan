package ar.edu.unicen.isistan.asistan.tracker.statemachine.states;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.map.DetailedMapContext;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachine;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions.Condition;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions.TimeCondition;

public class UncertainPassiveState extends State {

    private transient PassiveState passiveState;
    private transient ActiveState activeState;
    private transient UncertainActiveState uncertainActiveState;
    private transient Condition passiveCondition;

    @NotNull
    private List<Event> buffer;

    public UncertainPassiveState(@NotNull StateMachine state_machine, @NotNull String key, @NotNull DetailedMapContext mapContext) {
        super(state_machine, key, mapContext);
        this.buffer = new ArrayList<>();
        this.passiveCondition = new TimeCondition(90000L, this);
    }

    public void setStates(@NotNull PassiveState passiveState, @NotNull ActiveState activeState, @NotNull UncertainActiveState uncertainActiveState) {
        this.passiveState = passiveState;
        this.activeState = activeState;
        this.uncertainActiveState = uncertainActiveState;
    }

    @Override
    protected void abort() {
        if (this.passiveState.isOpen()) {
            this.passiveState.addAll(this.buffer);
            this.passiveState.close();
        }
        this.close();
    }

    public void load(@NotNull SharedPreferences preferences) {
        String json = preferences.getString(this.getKey(), null);
        if (json != null) {
            UncertainPassiveState aux = new Gson().fromJson(json, UncertainPassiveState.class);
            this.firstEvent.init(aux.firstEvent);
            this.lastEvent.init(aux.lastEvent);
            this.buffer = aux.buffer;
        }
    }

    protected void add(Event event) {
        if (this.lastEvent.getTime() != event.getTime()) {
            super.add(event);
            this.buffer.add(event.copy());
        }
    }

    @Override
    protected long inconsistentTime() {
        return TWENTY_FIVE_MINUTES;
    }

    @Override
    protected double distance(@NotNull Event event) {
        if (this.passiveState.getVisitingArea() != null)
            return Math.min(super.distance(event), this.passiveState.getVisitingArea().distance(event.getLocation()) - event.getAccuracy());
        else
            return super.distance(event);
    }

    @Override
    public State processEvent(@NotNull Event event) {
        if (this.toPassive(event)) {
            if (this.buffer.size() == 1) {
                this.passiveState.add(this.buffer.get(0));
                this.passiveState.add(event);
                this.close();
                return this.passiveState;
            } else {
                this.passiveState.add(this.buffer.remove(0));
                this.firstEvent.init(this.buffer.get(0));
                return this.processEvent(event);
            }
        } else if (this.toActive(event)) {
            if (this.passiveState.isOpen())
                this.activeState.add(this.passiveState.last());
            this.passiveState.close();
            if (!this.activeState.isOpen())
                this.activeState.add(this.buffer.remove(0));
            this.uncertainActiveState.addAll(this.buffer);
            this.uncertainActiveState.add(event);
            this.close();
            return this.uncertainActiveState;
        } else {
            this.add(event);
            return this;
        }
    }

    @Override
    protected void close() {
        super.close();
        this.buffer.clear();
    }

    private boolean toPassive(@NotNull Event event) {
        return (this.passiveState.getVisitingArea() != null && (this.passiveState.getVisitingArea().contains(event.getLocation())) || (this.passiveCondition.check(event) && this.passiveState.getVisitingArea().distance(event.getLocation()) < event.getAccuracy()));
    }

    private boolean toActive(@NotNull Event event) {
        return (this.passiveState.getVisitingArea() == null || this.passiveState.getVisitingArea().distance(event.getLocation()) > event.getAccuracy());
    }

    @Override
    public void clear() {
        super.close();
        this.buffer.clear();
    }

}
