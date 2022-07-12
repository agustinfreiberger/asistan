package ar.edu.unicen.isistan.asistan.tracker.statemachine.states;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.map.DetailedMapContext;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMArea;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.Parameters;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachine;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.IntersectionArea;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.UnionArea;

public class PassiveState extends State {

    private transient UncertainPassiveState uncertainState;

    @Nullable
    private Visit visit;
    @Nullable
    private Area visitingArea;

    public PassiveState(@NotNull StateMachine stateMachine, @NotNull String key, @NotNull DetailedMapContext mapContext) {
        super(stateMachine, key, mapContext);
        this.visit = null;
    }

    public void load(@NotNull SharedPreferences preferences) {
        String json = preferences.getString(this.getKey(),null);
        if (json != null) {
            PassiveState aux = new Gson().fromJson(json, PassiveState.class);
            this.firstEvent.init(aux.firstEvent);
            this.lastEvent.init(aux.lastEvent);
            this.visit = aux.visit;
            this.visitingArea = aux.visitingArea;
        }
    }

    public void setUncertainState(@NotNull UncertainPassiveState uncertainState) {
        this.uncertainState = uncertainState;
    }

    @Override
    public void add(@NotNull Event event) {
        if (this.lastEvent.getTime() != event.getTime()) {
            this.updateVisit(event);
            super.add(event);
            this.updateArea();
        }
    }

    @Override
    public State processEvent(@NotNull Event event) {
        if (this.stay(event)){
            this.add(event);
            return this;
        } else {
            this.uncertainState.add(event);
            return this.uncertainState;
        }
    }

    @Override
    protected void abort() {
        this.close();
    }

    @Override
    protected double distance(@NotNull Event event) {
        if (this.visitingArea != null)
            return Math.min(super.distance(event), this.visitingArea.distance(event.getLocation()) - event.getAccuracy());
        else
            return super.distance(event);
    }

    @Override
    protected long inconsistentTime() {
        return TWENTY_FIVE_MINUTES;
    }

    @Override
    public void close() {
        this.closeVisit();
        super.close();
    }

    @Nullable
    public Area getVisitingArea() {
        return this.visitingArea;
    }

    private void closeVisit() {
        if (this.visit != null) {
            this.visit.setEndTime(this.lastEvent.getTime());
            this.stateMachine.closeVisit(this.visit);
        }
        this.visit = null;
        this.visitingArea = null;
    }

    private void updateVisit(@NotNull Event event) {
        if (this.visit == null) {
            this.openVisit(event);
        } else {
            long total = this.lastEvent.getTime() - this.firstEvent.getTime();
            long new_total = event.getTime() - this.firstEvent.getTime();
            double weight = total / (double) new_total;
            double new_weight = (new_total - total) / (double) new_total;
            double new_lat = (event.getLocation().getLatitude() + this.lastEvent.getLocation().getLatitude()) / 2;
            double new_lng = (event.getLocation().getLongitude() + this.lastEvent.getLocation().getLongitude()) / 2;
            double latitude = (this.visit.getCenter().getLatitude() * weight) + (new_lat * new_weight);
            double longitude = (this.visit.getCenter().getLongitude() * weight) + (new_lng * new_weight);

            this.visit.setCenter(new Coordinate(latitude, longitude));
            this.visit.setEndTime(event.getTime());
            this.stateMachine.updateVisit(this.visit);
        }
    }

    private void openVisit(@NotNull Event event) {
        this.visit = new Visit();
        this.visit.setCenter(event.getLocation());
        this.visit.setStartTime(event.getTime());
        this.visit.setEndTime(event.getTime());
        this.stateMachine.openVisit(this.visit);
    }

    private boolean stay(@NotNull Event event) {
        return this.visitingArea != null && this.visitingArea.contains(event.getLocation());
    }

    private void updateArea() {
        Parameters parameters = this.stateMachine.getParameters();
        if (this.visit != null) {
            Place place = this.visit.getPlace();
            if (place != null) {
                this.visitingArea = new UnionArea(new Circle(this.visit.getCenter(), parameters.getMinRadius()), place.getArea());
            } else {
                OSMArea osmArea = this.mapContext.getOSMArea(this.visit.getCenter(), 10);
                if (osmArea != null) {
                    Area area = osmArea.getArea();
                    if (!osmArea.isBuilding() && area.getSurface() > parameters.getMaxSurface())
                        this.visitingArea = new IntersectionArea(new Circle(this.visit.getCenter(), parameters.getMaxRadius()), area);
                    else
                        this.visitingArea = new UnionArea(new Circle(this.visit.getCenter(), parameters.getMinRadius()), area);
                } else {
                    this.visitingArea = new Circle(this.visit.getCenter(), parameters.getMinRadius());
                }
            }
        }
    }

    @Override
    public void clear() {
        super.close();
        this.visit = null;
        this.visitingArea = null;
    }

    public void setVisit(@NotNull Visit visit) {
        this.visit = visit;
        Event event = new Event();
        event.setConfidence(100F);
        event.setAccuracy(30F);
        event.setLocation(visit.getCenter());
        event.setTime(visit.getStartTime());
        this.firstEvent.init(event);
        event.setTime(visit.getEndTime());
        this.lastEvent.init(event);
        this.updateArea();
    }

    @Nullable
    public Visit getVisit() {
        return this.visit;
    }

}
