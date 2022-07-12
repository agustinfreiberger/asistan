package ar.edu.unicen.isistan.asistan.tracker.statemachine.states;

import android.content.SharedPreferences;

import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.map.DetailedMapContext;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachine;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions.ActivityCondition;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions.Condition;

public class ActiveState extends State {

    private static final long FIVE_MINUTES = 300000L;

    private transient UncertainActiveState uncertainState;
    private transient PassiveState passiveState;
    private transient Condition activeCondition;

    @Nullable
    private Commute commute;

    public ActiveState(@NotNull StateMachine stateMachine, @NotNull String key, @NotNull DetailedMapContext mapContext) {
        super(stateMachine,key,mapContext);
        this.activeCondition = new ActivityCondition(75,DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE);
        this.commute = null;
    }

    @Override
    public void load(@NotNull SharedPreferences preferences) {
        String json = preferences.getString(this.getKey(), null);
        if (json != null) {
            ActiveState aux = new Gson().fromJson(json, ActiveState.class);
            this.firstEvent.init(aux.firstEvent);
            this.lastEvent.init(aux.lastEvent);
            this.commute = aux.commute;
        }
    }

    @Override
    protected long inconsistentTime() {
        return this.stateMachine.isTravelling() ? SIX_HOURS : TWENTY_FIVE_MINUTES;
    }

    @Override
    protected void abort() {
        this.close();
    }


    public void setUncertainState(@NotNull UncertainActiveState uncertainState) {
        this.uncertainState = uncertainState;
    }

    public void setPassiveState(@NotNull PassiveState passiveState) {
        this.passiveState = passiveState;
    }

    protected void add(@NotNull Event event) {
        if (this.lastEvent.getTime() != event.getTime()) {
            Event copy = event.copy(); // It is important to make a copy of the event
            super.add(copy);
            this.updateCommute(copy);
        }
    }

    @Override
    public State processEvent(@NotNull Event event) {
        if (this.lostSignal(event)) { // If lostSignal, open passive state with last two events
            this.passiveState.add(this.last().copy());
            this.passiveState.add(event);
            this.close();
            return this.passiveState;
        } else if (this.move(event)) {
            this.add(event);
            return this;
        } else {
            if (!this.last().isEmpty())
                this.uncertainState.add(this.last().copy());
            this.uncertainState.add(event);
            return this.uncertainState;
        }
    }

    @Override
    public void close() {
        if (this.commute != null)
            this.closeCommute();
        super.close();
    }

    private void updateCommute(@NotNull Event event) {
        if (this.commute == null) {
            this.openCommute(event);
        } else {
            this.commute.addEvent(event);
            this.commute.setEndTime(event.getTime());
            this.stateMachine.updateCommute(this.commute);
        }
    }

    private void openCommute(Event event) {
        this.commute = new Commute();
        this.commute.addEvent(event);
        this.commute.setStartTime(this.firstEvent.getTime());
        this.commute.setEndTime(this.lastEvent.getTime());
        this.stateMachine.openCommute(this.commute);
    }

    private void closeCommute() {
        if (this.commute != null)
            this.stateMachine.closeCommute(this.commute);
        this.commute = null;
    }

    private boolean lostSignal(@NotNull Event event) {
        return (event.getTime() - this.last().getTime() > FIVE_MINUTES && event.getLocation().distance(this.last().getLocation()) - event.getAccuracy() < MAX_DISTANCE);
    }

    public boolean move(@NotNull Event event) {
        if (this.activeCondition.check(event))
            return true;

        if (this.inArea(event.getLocation()))
            return false;

        // Commute should never be null, checking just in case
        return this.commute == null || this.commute.lastLocation().distance(event.getLocation()) > (this.stateMachine.getParameters().getMinRadius() * 2);
    }

    private boolean inArea(@NotNull Coordinate location) {
        return this.mapContext.inPlace(location) || this.mapContext.inArea(location);
    }

    @Override
    public void clear() {
        super.close();
        this.commute = null;
    }

    public void setCommute(@NotNull Commute commute) {
        this.commute = commute;
        ArrayList<Event> path = commute.getFullTravel();
        this.firstEvent.init(path.get(0));
        this.lastEvent.init(path.get(path.size()-1));
    }

    @Nullable
    public Commute getCommute() {
        return this.commute;
    }
}
