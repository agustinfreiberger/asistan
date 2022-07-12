package ar.edu.unicen.isistan.asistan.tracker.statemachine;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unicen.isistan.asistan.map.DetailedMapContext;
import ar.edu.unicen.isistan.asistan.map.MapManager;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes.Commute;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.labels.Labeler;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.VisitCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSM;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMArea;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.states.ActiveState;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.states.PassiveState;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.states.State;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.states.UncertainActiveState;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.states.UncertainPassiveState;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;

public class StateMachine {

    private static final String PASSIVE_STATE = "passive_state";
    private static final String ACTIVE_STATE = "active_state";
    private static final String UNCERTAIN_PASSIVE_STATE = "uncertain_passive_state";
    private static final String UNCERTAIN_ACTIVE_STATE = "uncertain_active_state";
    private static final String TRAVELLING = "travelling";
    private static final String FIXED_VISIT = "fixed_visit";

    private boolean fixedVisit;
    private boolean travelling;

    @NotNull
    private final ArrayList<Visit> visits;
    @NotNull
    private final ArrayList<Commute> commutes;
    @NotNull
    private final Parameters parameters;
    @NotNull
    private final UncertainActiveState uncertainActiveState;
    @NotNull
    private final UncertainPassiveState uncertainPassiveState;
    @NotNull
    private final ActiveState activeState;
    @NotNull
    private final PassiveState passiveState;
    @NotNull
    private State currentState;
    @NotNull
    private final DetailedMapContext detailedMapContext;

    public StateMachine() {
        this.detailedMapContext = new DetailedMapContext();
        this.parameters = Parameters.defaultParameters();

        this.visits = new ArrayList<>();
        this.commutes = new ArrayList<>();

        this.passiveState = new PassiveState(this, PASSIVE_STATE, detailedMapContext);
        this.activeState = new ActiveState(this, ACTIVE_STATE, detailedMapContext);
        this.uncertainPassiveState = new UncertainPassiveState(this, UNCERTAIN_PASSIVE_STATE, detailedMapContext);
        this.uncertainActiveState = new UncertainActiveState(this, UNCERTAIN_ACTIVE_STATE, detailedMapContext);

        this.uncertainPassiveState.setStates(this.passiveState, this.activeState,this.uncertainActiveState);
        this.passiveState.setUncertainState(this.uncertainPassiveState);
        this.uncertainActiveState.setStates(this.activeState, this.passiveState);
        this.activeState.setUncertainState(this.uncertainActiveState);
        this.activeState.setPassiveState(this.passiveState);

        this.currentState = this.uncertainActiveState;
    }

    public void load(@NotNull SharedPreferences sharedPreferences) {
        this.passiveState.load(sharedPreferences);
        this.activeState.load(sharedPreferences);
        this.uncertainPassiveState.load(sharedPreferences);
        this.uncertainActiveState.load(sharedPreferences);

        ArrayList<State> aux = new ArrayList<>();
        aux.add(this.passiveState);
        aux.add(this.activeState);
        aux.add(this.uncertainPassiveState);
        aux.add(this.uncertainActiveState);

        this.currentState = selectState(aux);

        this.travelling = sharedPreferences.getBoolean(TRAVELLING, false);
        this.fixedVisit = sharedPreferences.getBoolean(FIXED_VISIT, false);

        if (!this.currentState.last().isEmpty())
            this.detailedMapContext.update(this.currentState.last().getLocation());
    }

    public boolean save(@NotNull SharedPreferences preferences) {
        try {
            for (Visit visit : this.visits)
                this.store(visit);
            for (Commute commute : this.commutes)
                this.store(commute);
            this.visits.clear();
            this.commutes.clear();
            SharedPreferences.Editor editor = preferences.edit();
            this.uncertainActiveState.save(editor);
            this.uncertainPassiveState.save(editor);
            this.passiveState.save(editor);
            this.activeState.save(editor);
            editor.putBoolean(TRAVELLING, this.travelling);
            return editor.commit();
        } catch (Exception ignored) {
            return false;
        }
    }

    private void store(@NotNull Commute commute) {
        if (commute.hasId())
            Database.getInstance().mobility().update(commute);
        else
            Database.getInstance().mobility().insert(commute);

    }

    private void store(@NotNull Visit visit) {
        if (visit.isClosed()) {
            if (visit.hasPlace())
                updatePlace(visit);
            else
                createPlace(visit);
        }

        if (visit.hasId())
            Database.getInstance().mobility().update(visit);
        else
            Database.getInstance().mobility().insert(visit);
    }

    private void updatePlace(@NotNull Visit visit) {
        Place place = visit.getPlace();
        if (place != null && !place.isFixedLocation() && place.getArea().getType().equals(Area.AreaType.CIRCLE)) {
            Circle circle = (Circle) place.getArea();
            int count = Database.getInstance().mobility().countVisits(visit.getPlaceId());
            double latitude = count == 0 ? visit.getCenter().getLatitude() : ((circle.getCenter().getLatitude() * (count-1) + visit.getCenter().getLatitude()) / count);
            double longitude = count == 0 ? visit.getCenter().getLongitude() : ((circle.getCenter().getLongitude() * (count-1) + visit.getCenter().getLongitude()) / count);
            circle.setCenter(new Coordinate(latitude,longitude));
            place.setArea(circle);
            place.setUpload(true);
            Database.getInstance().mobility().update(place);
        }
    }

    private void createPlace(@NotNull Visit visit) {
        Place place = new Place();
        place.setArea(new Circle(visit.getCenter(),30));
        place.setUpload(true);

        List<OSMPlace> places = MapManager.getInstance().getOSMPlaces(visit.getCenter(),10);
        if (places.size() == 1 && places.get(0).getLocation().distance(visit.getCenter()) <= 5) {
            OSM osmPlace = places.get(0);
            if (osmPlace.getName() != null)
                osmPlace.export(place);
        } else {
            Database.getInstance().openStreetMap().nearAreas(visit.getCenter(),0);
            OSMArea osmArea = MapManager.getInstance().getOSMArea(visit.getCenter());
            if (osmArea != null)
                osmArea.export(place);
        }

        Database.getInstance().mobility().insert(place);
        visit.setPlace(place);
    }

    @NotNull
    public Parameters getParameters() {
        return parameters;
    }

    @NotNull
    private State selectState(ArrayList<State> aux) {
        State selected = null;
        for (State state: aux) {
            Event event = state.last();
            if (!event.isEmpty() && (selected == null || event.after(selected.last())))
                selected = state;
        }

        if (selected == null)
            selected = this.uncertainActiveState;

        return selected;
    }

    public boolean isTravelling() {
        return travelling;
    }

    public void setTravelling(@NotNull SharedPreferences preferences, boolean travelling) {
        this.travelling = travelling;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(TRAVELLING, travelling);
        editor.apply();
    }

    public void setFixedVisit(@NotNull SharedPreferences preferences, boolean fixedVisit) {
        this.fixedVisit = fixedVisit;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIXED_VISIT, fixedVisit);
        editor.apply();
    }

    public void openCommute(@NotNull Commute commute) {
        this.addCommute(commute);
    }

    public void updateCommute(@NotNull Commute commute) {
        if (commute.hasId())
            this.addCommute(commute);
    }

    public void closeCommute(@NotNull Commute commute) {
        commute.close();
        new Labeler().label(commute);
        if (commute.hasId())
            this.addCommute(commute);
    }

    private void addCommute(@NotNull Commute commute) {
        if (!this.commutes.contains(commute))
            this.commutes.add(commute);
    }

    public void openVisit(@NotNull Visit visit) {
        this.fixedVisit = false;
        this.check(visit);
        this.addVisit(visit);
    }

    public void updateVisit(@NotNull Visit visit) {
        this.check(visit);
        if (visit.hasId())
            this.addVisit(visit);
    }

    public void closeVisit(@NotNull Visit visit) {
        visit.close();
        this.check(visit);

        new Labeler().label(visit);
        if (visit.hasId())
            this.addVisit(visit);

        this.fixedVisit = false;
    }

    private void check(@NotNull Visit visit) {
        if (!this.fixedVisit) {
            Place visitedPlace = this.detailedMapContext.getPlace(visit.getCenter());
            visit.setPlace(visitedPlace);
            visit.setCategory(VisitCategory.VISIT.getCode());
        }
    }

    private void addVisit(@NotNull Visit visit) {
        if (!this.visits.contains(visit))
            this.visits.add(visit);
    }

    public boolean executeStates(Event event) {
        boolean change;
        State next = this.currentState.newEvent(event);
        if (next != null) {
            change = this.currentState != next;
            this.currentState = next;
        } else {
            change = this.currentState != this.uncertainActiveState;
            this.currentState = this.uncertainActiveState;
            this.executeStates(event);
        }
        return change;
    }

    @NotNull
    public Event last() {
        return this.currentState.last();
    }

    public void setCommute(@NotNull Commute commute) {
        this.passiveState.clear();
        this.uncertainPassiveState.clear();
        this.uncertainActiveState.clear();
        this.activeState.clear();
        this.activeState.setCommute(commute);
    }

    public void setVisit(@NotNull Visit visit) {
        this.clear();
        this.passiveState.setVisit(visit);
        this.currentState = this.passiveState;
    }

    public boolean current(@NotNull Commute commute) {
        Commute current = this.getCurrentCommute();
        return (current != null && current.getId() == commute.getId());
    }

    public boolean current(@NotNull Visit visit) {
        Visit current = this.getCurrentVisit();
        return (current != null && current.getId() == visit.getId());
    }

    public void clear() {
        this.passiveState.clear();
        this.uncertainPassiveState.clear();
        this.uncertainActiveState.clear();
        this.activeState.clear();
        this.currentState = this.uncertainActiveState;
    }

    @Nullable
    public Visit getCurrentVisit() {
        return this.passiveState.getVisit();
    }

    @Nullable
    public Commute getCurrentCommute() {
        return this.activeState.getCommute();
    }

}
