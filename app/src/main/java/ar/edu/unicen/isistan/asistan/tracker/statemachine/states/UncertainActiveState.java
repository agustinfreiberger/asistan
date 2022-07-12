package ar.edu.unicen.isistan.asistan.tracker.statemachine.states;

import android.content.SharedPreferences;

import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.map.DetailedMapContext;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMArea;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.Parameters;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachine;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions.ActivityCondition;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.conditions.Condition;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.UnionArea;

public class UncertainActiveState extends State {

    private static final long FIVE_MINUTES = 300000L;

    private transient ActiveState activeState;
    private transient PassiveState passiveState;
    private transient Condition activeCondition;

    @NotNull
    private List<Event> buffer;
    @Nullable
    private Coordinate center;
    @Nullable
    private Area area;
    private int activeEvents;
    private boolean knownPlace;

    public UncertainActiveState(@NotNull StateMachine stateMachine, @NotNull String key, @NotNull DetailedMapContext mapContext) {
        super(stateMachine, key, mapContext);
        this.buffer = new ArrayList<>();
        this.center = null;
        this.activeEvents = 0;
        this.knownPlace = false;
        this.activeCondition = new ActivityCondition(75,DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE);
    }

    public void setStates(@NotNull ActiveState activeState, @NotNull PassiveState passiveState) {
        this.activeState = activeState;
        this.passiveState = passiveState;
        this.area = null;
    }

    @Override
    protected long inconsistentTime() {
        return this.stateMachine.isTravelling() ? SIX_HOURS : TWENTY_FIVE_MINUTES;
    }

    @Override
    protected void abort() {
        if (this.activeState.isOpen()) {
            this.activeState.addAll(this.buffer);
            this.activeState.close();
        }
        this.close();
    }

    public void load(@NotNull SharedPreferences preferences) {
        String json = preferences.getString(this.getKey(), null);
        if (json != null) {
            UncertainActiveState aux = new Gson().fromJson(json, UncertainActiveState.class);
            this.firstEvent.init(aux.firstEvent);
            this.lastEvent.init(aux.lastEvent);
            this.buffer = aux.buffer;
            this.area = aux.area;
            this.activeEvents = aux.activeEvents;
            this.knownPlace = aux.knownPlace;
        }
    }

    protected void add(@NotNull Event event) {
        if (this.lastEvent.getTime() != event.getTime()) {
            super.add(event);
            this.addEvent(event);
        }
    }

    @Override
    public State processEvent(@NotNull Event event) {
        if (this.lostSignal(event)) {
            if (this.activeState.isOpen())
                this.activeState.add(this.buffer.get(0));
            this.activeState.close();
            for (Event bufferedEvent: this.buffer)
                this.passiveState.add(bufferedEvent);
            this.passiveState.add(event);
            this.close();
            return this.passiveState;
        }

        this.area = calculateArea(event);

        if (this.toPassive(event)) {
            if (this.activeState.isOpen())
                this.activeState.add(this.firstEvent);
            this.activeState.close();
            this.passiveState.addAll(this.buffer);
            this.passiveState.add(event);
            this.close();
            return this.passiveState;
        } else if (this.toActive()) {
            if (this.buffer.size() == 1) {
                this.activeState.add(this.buffer.get(0));
                this.activeState.add(event);
                this.close();
                return this.activeState;
            } else {
                this.activeState.add(this.buffer.get(0));
                this.remove();
                return this.processEvent(event);
            }
        } else{
            this.add(event);
            return this;
        }

    }

    @Override
    protected void close() {
        super.close();
        this.buffer.clear();
        this.center = null;
        this.area = null;
        this.activeEvents = 0;
    }

    private boolean lostSignal(@NotNull Event event) {
        return (event.getTime() - this.last().getTime() > FIVE_MINUTES && event.getLocation().distance(this.last().getLocation()) - event.getAccuracy() < MAX_DISTANCE);
    }

    private boolean toPassive(@NotNull Event event) {
        if (this.area == null)
            return false;
        long time = this.neededTime(this.area);
        return this.elapsedTime(event) >= time;
    }

    private long elapsedTime(@NotNull  Event event) {
        return event.getTime() - this.buffer.get(0).getTime();
    }

    private boolean toActive() {
        return (this.area == null);
    }

    @Nullable
    private Area calculateArea(@NotNull Event event) {
        Coordinate nextCenter = this.calculateCenter(event);

        Place place = this.mapContext.getPlace(nextCenter);
        this.knownPlace = place != null;

        Parameters parameters = this.stateMachine.getParameters();
        Area area = new Circle(nextCenter,parameters.getMinRadius());
        if (this.bufferInside(area) && area.contains(event.getLocation()))
            return area;

        if (place != null) {
            area = new UnionArea(area,place.getArea());
            if (this.bufferInside(area) && area.contains(event.getLocation()))
                return area;
        } else {
            OSMArea osmArea = this.mapContext.getOSMArea(nextCenter);
            if (osmArea != null) {
                if (osmArea.isBuilding() || osmArea.getArea().getSurface() > parameters.getMaxSurface()) {
                    area = new UnionArea(area, osmArea.getArea());
                    if (area.contains(event.getLocation()) && this.bufferInside(area)) {
                        return area;
                    }
                } else {
                    if (osmArea.getArea().contains(event.getLocation()) && this.bufferInside(osmArea.getArea())) {
                        ArrayList<Coordinate> coordinates = new ArrayList<>();
                        for (Event buffered : this.buffer)
                            coordinates.add(buffered.getLocation());
                        coordinates.add(event.getLocation());

                        Circle circle = Circle.makeCircle(nextCenter, coordinates);
                        if (circle.getRadius() <= parameters.getMaxRadius())
                            return circle;
                    }
                }
            }
        }

        return null;
    }

    private boolean bufferInside(@NotNull Area area) {
        for (Event event: this.buffer) {
            if (!area.contains(event.getLocation()))
                return false;
        }
        return true;
    }

    private long neededTime(@NotNull Area area) {
        long time;
        double surface = area.getSurface();
        Parameters parameters = this.stateMachine.getParameters();
        if (surface >= parameters.getMaxSurface())
            time = parameters.getMaxTime();
        else if (surface <= parameters.getMinSurface())
            time = parameters.getMinTime();
        else {
            double percentage = (surface - parameters.getMinSurface()) / (parameters.getMaxSurface()-parameters.getMinSurface());
            time = parameters.getMinTime() + (long) ((parameters.getMaxTime()-parameters.getMinTime())*percentage);
        }

        if (this.knownPlace) {
            time -= parameters.getKnownPlaceBenefit();
        } else if (this.activeEvents > 0) {
            time += parameters.getActivePenalty();
            time = Math.min(time, parameters.getMaxTime());
        }
        return time;
    }

    private Coordinate calculateCenter(@NotNull Event event) {
        if (this.buffer.isEmpty() || this.center == null) {
            return new Coordinate(event.getLocation().getLatitude(), event.getLocation().getLongitude());
        } else {
            Event aux = this.buffer.get(this.buffer.size() - 1);
            long bufferTime = this.buffer.get(this.buffer.size() - 1).getTime() - this.buffer.get(0).getTime();
            double lat = this.center.getLatitude() * bufferTime;
            double lng = this.center.getLongitude() * bufferTime;
            long newTime = event.getTime() - this.buffer.get(this.buffer.size() - 1).getTime();
            lat += ((event.getLocation().getLatitude() + aux.getLocation().getLatitude()) / 2) * newTime;
            lng += ((event.getLocation().getLongitude() + aux.getLocation().getLongitude()) / 2) * newTime;
            lat /= (bufferTime + newTime);
            lng /= (bufferTime + newTime);
            return new Coordinate(lat, lng);
        }
    }

    private void addEvent(@NotNull Event event) {
        this.center = calculateCenter(event);
        this.buffer.add(event.copy());
        if (this.activeCondition.check(event))
            this.activeEvents++;
    }

    private void remove() {
        if (this.center != null) {
            if (this.buffer.size() == 2) {
                this.center.setLatitude(this.buffer.get(1).getLocation().getLatitude());
                this.center.setLongitude(this.buffer.get(1).getLocation().getLongitude());
            } else {
                Event aux = this.buffer.get(1);
                long eventTime = aux.getTime() - this.buffer.get(0).getTime();
                long bufferTime = this.buffer.get(this.buffer.size() - 1).getTime() - this.buffer.get(1).getTime();
                double lat = this.center.getLatitude() * (bufferTime + eventTime);
                double lng = this.center.getLongitude() * (bufferTime + eventTime);
                lat -= ((firstEvent.getLocation().getLatitude() + aux.getLocation().getLatitude()) / 2) * eventTime;
                lng -= ((firstEvent.getLocation().getLongitude() + aux.getLocation().getLongitude()) / 2) * eventTime;
                lat /= bufferTime;
                lng /= bufferTime;
                this.center.setLatitude(lat);
                this.center.setLongitude(lng);
            }
        }

        Event event = this.buffer.remove(0);
        this.activeState.add(event);
        this.firstEvent.init(this.buffer.get(0));
        if (this.activeCondition.check(event))
            this.activeEvents--;
    }

    @Override
    public void clear() {
        super.close();
        this.buffer.clear();
        this.center = null;
        this.activeEvents = 0;
        this.knownPlace = false;
        this.area = null;
    }

}
