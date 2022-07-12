package ar.edu.unicen.isistan.asistan.storage.database.mobility.events;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.location.DetectedActivity;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.utils.queues.Reusable;

@Entity(tableName = Event.TABLE_NAME)
public class Event implements Comparable<Event>, Reusable<Event> {

    public static final String TABLE_NAME = "event";

    private static final long NO_DATA = -1L;
    @PrimaryKey
    @ColumnInfo(name = "time")
    private long time;
    @Embedded
    @NotNull
    private final Coordinate location;
    @ColumnInfo(name = "accuracy")
    private float accuracy;
    @ColumnInfo(name = "activity")
    private int activity;
    @ColumnInfo(name = "confidence")
    private double confidence;

    public Event() {
        this.location = new Coordinate();
        this.init();
    }

    public Event(Activity activity, GeoLocation location) {
        this();
        this.init(activity,location);
    }

    public Event(@NotNull Event event) {
        this();
        this.init(event);
    }

    public void init(@Nullable Activity activity, @NotNull GeoLocation location) {
        this.time = location.getLocTime();
        this.location.init(location.getLatitude(),location.getLongitude());
        this.accuracy = location.getAccuracy();
        if (activity != null) {
            this.activity = activity.getType();
            this.confidence = activity.getConfidence();
        } else {
            this.activity = DetectedActivity.UNKNOWN;
            this.confidence = 100;
        }
    }

    @Override
    public void init() {
        this.time = NO_DATA;
        this.accuracy = 0F;
        this.accuracy = DetectedActivity.UNKNOWN;
        this.confidence = 0D;
        this.location.init();
    }

    public void init(@Nullable Event event) {
        if (event != null) {
            this.time = event.getTime();
            this.location.init(event.getLocation());
            this.accuracy = event.getAccuracy();
            this.activity = event.getActivity();
            this.confidence = event.getConfidence();
        } else {
            this.init();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.time == NO_DATA;
    }

    @NotNull
    public Event copy() {
        return new Event(this);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    @NotNull
    public Coordinate getLocation() {
        return this.location;
    }

    public void setLocation(@NotNull Coordinate location) {
        this.location.init(location);
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public boolean after(@NotNull Event event) {
        return this.time > event.time;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (o instanceof Event) {
            Event event = (Event) o;
            return this.getTime() == event.getTime();
        }
        return false;
    }

    @Override
    public int compareTo(Event o) {
        if (this.getTime() < o.getTime())
            return -1;
        else if (this.getTime() > o.getTime())
            return 1;
        else
            return 0;
    }

}
