package ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.Movement;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.storage.database.reports.userstate.movement.UserCommute;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

@Entity(tableName = Commute.TABLE_NAME)
public class Commute extends Movement {

    public static final String TABLE_NAME = "commute";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name="steps")
    @TypeConverters(StepListConverter.class)
    private ArrayList<Step> steps;
    @ColumnInfo(name = "category")
    private int category;
    @Ignore
    @Nullable
    private Visit origin;
    @Ignore
    @Nullable
    private Visit destination;

    public Commute() {
        super(MovementType.COMMUTE);
        this.steps = new ArrayList<>();
        this.steps.add(new Step());
        this.category = CommuteCategory.COMMUTE.getCode();
        this.origin = null;
        this.destination = null;
    }

    public Commute(@NotNull Commute previous, @NotNull Commute next) {
        this();

        ArrayList<Event> fullTravel = new ArrayList<>();
        fullTravel.addAll(previous.getFullTravel());
        fullTravel.addAll(next.getFullTravel());
        this.steps.get(0).addEvents(fullTravel);
        this.setStartTime(previous.getStartTime());
        this.setEndTime(next.getEndTime());

        this.labels.addAll(previous.getLabels());
        for (Integer label: next.getLabels()) {
            if (!this.labels.contains(label))
                this.labels.add(label);
        }

        if (next.isClosed())
            this.close();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean hasId() {
        return this.id != 0;
    }

    public void addEvent(@NotNull Event event) {
        this.steps.get(this.steps.size()-1).addEvent(event);
    }

    @NotNull
    public ArrayList<Event> getFullTravel() {
        ArrayList<Event> events = new ArrayList<>();
        for (int index = 0; index < this.steps.size(); index++) {
            Step step = this.steps.get(index);
            if (index != 0)
                events.remove(events.size()-1);
            events.addAll(step.getEvents());
        }
        return events;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Nullable
    public Visit getOrigin() {
        return origin;
    }

    public void setOrigin(@Nullable Visit origin) {
        this.origin = origin;
    }

    @Nullable
    public Visit getDestination() {
        return destination;
    }

    public void setDestination(@Nullable Visit destination) {
        this.destination = destination;
    }

    @Override
    public void close() {
        super.close();
        this.analyzeSteps();
    }

    @Override
    public UserCommute simplify() {
        UserCommute commute = new UserCommute();
        commute.setStartTime(this.getStartTime());
        commute.setEndTime(this.getEndTime());
        commute.setSteps(this.getSteps());
        commute.setClosed(this.isClosed());
        return commute;
    }

    public double distance() {
        double distance = 0;
        ArrayList<Event> events = this.getFullTravel();
        for (int index = 1; index < events.size();index++) {
            distance += events.get(index-1).getLocation().distance(events.get(index).getLocation());
        }
        if (events.size() > 1) {
            if (this.origin != null) {
                double distanceA = this.origin.getCenter().distance(events.get(0).getLocation());
                double distanceB = this.origin.getCenter().distance(events.get(1).getLocation());
                double distanceC = events.get(0).getLocation().distance(events.get(1).getLocation());
                if (distanceB < distanceC)
                    distance += distanceB - distanceC;
                else
                    distance += distanceA;
            }

            if (this.destination != null) {
                double distanceA = this.destination.getCenter().distance(events.get(events.size()-1).getLocation());
                double distanceB = this.destination.getCenter().distance(events.get(events.size()-2).getLocation());
                double distanceC = events.get(events.size()-1).getLocation().distance(events.get(events.size()-2).getLocation());
                if (distanceB < distanceC)
                    distance += distanceB - distanceC;
                else
                    distance += distanceA;
            }
        }
        return distance;
    }

    public void setSteps(@NotNull ArrayList<Step> steps) {
        this.steps.clear();
        for (Step step: steps)
            this.steps.add(step.copy());
        this.simplifySteps();
    }

    private void simplifySteps() {
        int aux = 0;
        while (aux < this.steps.size() - 1) {
            if (this.steps.get(aux).transportMode().equals(this.steps.get(aux + 1).transportMode())) {
                this.steps.get(aux).join(this.steps.get(aux+1));
                this.steps.remove(aux + 1);
            } else {
                aux++;
            }
        }
    }

    @NotNull
    public ArrayList<Step> getSteps() {
        if (this.needAnalysis())
            this.analyzeSteps();
        ArrayList<Step> steps = new ArrayList<>();
        for (Step step: this.steps)
            steps.add(step.copy());
        return steps;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (o instanceof Commute) {
            Commute commute = (Commute) o;
            return (commute.hasId() && this.hasId() && commute.getId() == this.getId());
        }
        return false;
    }

    public boolean different(@NotNull Commute commute) {
        return ((this.getId() != commute.getId()) ||
                (!this.getSteps().equals(commute.getSteps())) ||
                (this.getCategory() != commute.getCategory()) ||
                (this.getStartTime() != commute.getStartTime()) ||
                (this.getEndTime() != commute.getEndTime()) ||
                (this.isClosed() != commute.isClosed()) ||
                (!this.getLabels().equals(commute.getLabels())));
    }

    @NotNull
    public TransportMode transportMode() {
        if (this.needAnalysis())
            this.analyzeSteps();

        if (this.steps.size() > 1)
            return TransportMode.MIXED;
        else
            return this.steps.get(0).transportMode();
    }

    private boolean needAnalysis() {
        return this.steps.size() == 1 && this.steps.get(0).transportMode().equals(TransportMode.UNSPECIFIED);
    }

    private void analyzeSteps() {
        CommuteAnalyzer analyzer = new CommuteAnalyzer(this);
        analyzer.estimateSteps();
    }

    public void load(@NotNull Commute commute) {
        this.setId(commute.getId());
        this.setStartTime(commute.getStartTime());
        this.setEndTime(commute.getEndTime());
        this.setSteps(commute.getSteps());
        this.setCategory(commute.getCategory());
        this.setDestination(commute.getDestination());
        this.setOrigin(commute.getOrigin());
        this.setUpload(commute.isUpload());
        this.setLabels(commute.getLabels());
    }

    @NotNull
    public Commute copy() {
        Commute commute = new Commute();
        commute.setId(this.getId());
        commute.setStartTime(this.getStartTime());
        commute.setEndTime(this.getEndTime());
        commute.setSteps(this.getSteps());
        commute.setCategory(this.getCategory());
        commute.setDestination(this.getDestination());
        commute.setOrigin(this.getOrigin());
        commute.setUpload(this.isUpload());
        commute.setClosed(this.isClosed());
        commute.setLabels(new ArrayList<>(this.getLabels()));
        return commute;
    }

    @NotNull
    public Coordinate lastLocation() {
        Step step = this.steps.get(this.steps.size()-1);
        return step.getEvents().get(step.getEvents().size()-1).getLocation();
    }

    public boolean mayBeAnError() {
        return this.isClosed() && (this.getOrigin() == null || this.getDestination() == null || (this.getOrigin().hasPlace() && this.getDestination().hasPlace() && this.getOrigin().getPlaceId() == this.getDestination().getPlaceId()));
    }

    public Bound getBound() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Step step: this.steps)
            for (Event event: step.getEvents())
                coordinates.add(event.getLocation());
        return new Bound(coordinates);
    }

}
