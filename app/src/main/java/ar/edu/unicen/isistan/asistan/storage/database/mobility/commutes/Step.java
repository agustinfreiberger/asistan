package ar.edu.unicen.isistan.asistan.storage.database.mobility.commutes;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;

public class Step {

    @NotNull
    private ArrayList<Event> events;
    private int transportMode;
    private int estimatedTransportMode;

    public Step() {
        this.events = new ArrayList<>();
        this.transportMode = TransportMode.UNSPECIFIED.getCode();
        this.estimatedTransportMode = TransportMode.UNSPECIFIED.getCode();
    }

    public Step(@NotNull ArrayList<Event> events, TransportMode transportMode, TransportMode estimatedTransportMode) {
        this.events = new ArrayList<>(events);
        this.transportMode = transportMode.getCode();
        this.estimatedTransportMode = estimatedTransportMode.getCode();
    }

    @NotNull
    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(@NotNull ArrayList<Event> events) {
        this.events.clear();
        this.events.addAll(events);
    }

    @NotNull
    public TransportMode getTransportMode() {
        return TransportMode.get(this.transportMode);
    }

    public void setTransportMode(@Nullable TransportMode transportMode) {
        if (transportMode != null)
            this.transportMode = transportMode.getCode();
        else
            this.transportMode = TransportMode.UNSPECIFIED.getCode();
    }

    @NotNull
    public TransportMode getEstimatedTransportMode() {
        return TransportMode.get(this.estimatedTransportMode);
    }

    public void setEstimatedTransportMode(@Nullable TransportMode estimatedTransportMode) {
        if (estimatedTransportMode != null)
            this.estimatedTransportMode = estimatedTransportMode.getCode();
        else
            this.estimatedTransportMode = TransportMode.UNSPECIFIED.getCode();
    }

    public double distance() {
        double distance = 0;
        for (int index = 1; index < this.events.size();index++) {
            distance += this.events.get(index-1).getLocation().distance(this.events.get(index).getLocation());
        }
        return distance;
    }

    public double distanceBetween(long start, long end) {
        double distance = 0;
        for (int index = 1; index < this.events.size();index++) {
            if (this.events.get(index).getTime() >= start && this.events.get(index).getTime() <= end)
                distance += this.events.get(index-1).getLocation().distance(this.events.get(index).getLocation());
        }
        return distance;
    }

    public double distanceUntil(long time) {
        double distance = 0;
        for (int index = 1; index < this.events.size();index++) {
            if (this.events.get(index).getTime() <= time)
                distance += this.events.get(index-1).getLocation().distance(this.events.get(index).getLocation());
        }
        return distance;
    }

    public double distanceSince(long time) {
        double distance = 0;
        for (int index = 1; index < this.events.size();index++) {
            if (this.events.get(index).getTime() >= time)
                distance += this.events.get(index-1).getLocation().distance(this.events.get(index).getLocation());
        }
        return distance;
    }

    @NotNull
    public TransportMode transportMode() {
        if (this.transportMode != TransportMode.UNSPECIFIED.getCode())
            return TransportMode.get(this.transportMode);
        return TransportMode.get(this.estimatedTransportMode);
    }

    public long getStartTime() {
        return this.events.get(0).getTime();
    }

    public long getEndTime() {
        return this.events.get(this.events.size()-1).getTime();
    }

    public long getDuration() {
        return this.getEndTime() - this.getStartTime();
    }

    public void addEvents(Collection<Event> events) {
        this.events.addAll(events);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Step))
            return false;

        Step step = (Step) obj;
        return this.transportMode().equals(step.transportMode()) && this.getEvents().equals(step.getEvents());
    }

    public void addEvent(@NotNull Event event) {
        this.events.add(event);
    }

    public void join(@NotNull Step step) {
        this.events.addAll(step.getEvents());
    }

    @NotNull
    public Step copy() {
        Step step = new Step();
        step.setTransportMode(this.getTransportMode());
        step.setEstimatedTransportMode(this.getEstimatedTransportMode());
        step.setEvents(new ArrayList<>(this.getEvents()));
        return step;
    }

}
