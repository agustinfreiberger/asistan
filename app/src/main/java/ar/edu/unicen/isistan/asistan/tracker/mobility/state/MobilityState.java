package ar.edu.unicen.isistan.asistan.tracker.mobility.state;

import android.os.SystemClock;

import com.google.android.gms.location.DetectedActivity;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;

public class MobilityState {

    private static final int STILL_ACTIVITY_THRESHOLD = 90;
    private static final long TWENTY_SECONDS = 20000L;
    private static final long ONE_MINUTE = 60000L;
    private static final long THREE_MINUTES = 180000L;
    private static final double TWENTY_KILOMETERS_PER_HOUR = 5.55556;
    private static final double SEVEN_KILOMETERS_PER_HOUR = 1.94444;
    private static final double THREE_KILOMETERS_PER_HOUR = 0.833333;
    private static final double ONE_THIRD_KILOMETER_PER_HOUR = 0.0925;

    public static final int MOVING_FAST = 0;
    public static final int MOVING_SLOW = 1;
    public static final int STILL = 2;
    public static final int MOVING_LITTLE_FAST = 3;

    @NotNull
    private final Activity activity;
    @NotNull
    private final GeoLocation location;

    private int state;
    private long stateTime;
    private long last;

    public MobilityState() {
        this.activity = new Activity();
        this.location = new GeoLocation();
        this.init();
    }

    public void init() {
        this.state = MOVING_SLOW;
        this.stateTime = SystemClock.elapsedRealtime();
        this.last = 0;
        this.activity.init();
        this.location.init();
    }

    public int getState() {
        return this.state;
    }

    public long getStateTime() {
        return stateTime;
    }

    @NotNull
    private Activity getActivity() {
        return this.activity;
    }

    @NotNull
    private GeoLocation getLocation() {
        return this.location;
    }

    private long getLast() {
        return this.last;
    }

    private int getActivityType() {
        if (this.activity.isEmpty())
            return DetectedActivity.UNKNOWN;
        return this.activity.getType();
    }

    public void update(@NotNull Activity activity) {
        this.activity.init(activity);
        int code = this.getActivityType();
        long delta = activity.getElapsedTime() - this.last;

        switch (code) {
            case DetectedActivity.IN_VEHICLE:
                this.movingFast();
                break;
            case DetectedActivity.ON_BICYCLE:
            case DetectedActivity.RUNNING:
                if (this.state == MOVING_FAST)
                    this.movingFast();
                else
                    this.movingLittleFast();
                break;
            case DetectedActivity.WALKING:
            case DetectedActivity.ON_FOOT:
                if ((this.state != MOVING_FAST && this.state != MOVING_LITTLE_FAST) || delta >= ONE_MINUTE)
                    this.movingSlow();
                break;
            case DetectedActivity.UNKNOWN:
                if (this.state != MOVING_FAST && this.state != MOVING_LITTLE_FAST)
                    this.movingSlow();
                break;
            case DetectedActivity.STILL:
                if ((this.state != MOVING_FAST && this.state != MOVING_LITTLE_FAST) || delta > ONE_MINUTE) {
                    if (this.activity.getConfidence() < STILL_ACTIVITY_THRESHOLD)
                        this.movingSlow();
                    else
                        this.still();
                }
                break;
            case DetectedActivity.TILTING:
                break;
        }
    }

    public void update(@NotNull GeoLocation geoLocation) {
        if (!this.location.isEmpty()) {
            double distance = geoLocation.distance(this.location);
            long time = (geoLocation.getLocElapsedTime() - this.location.getLocElapsedTime()) / 1000;
            this.location.init(geoLocation);
            double velocity = distance / time;
            if (velocity > TWENTY_KILOMETERS_PER_HOUR)
                this.movingFast();
            else if (velocity > SEVEN_KILOMETERS_PER_HOUR) {
                if (this.state == MOVING_FAST)
                    this.movingFast();
                else
                    this.movingLittleFast();
            } else if (velocity < THREE_KILOMETERS_PER_HOUR && (this.state == MOVING_FAST || this.state == MOVING_LITTLE_FAST) && geoLocation.getElapsedTime() - this.last > TWENTY_SECONDS) {
                this.movingSlow();
            } else if (this.state != STILL && velocity < ONE_THIRD_KILOMETER_PER_HOUR && geoLocation.getElapsedTime() - this.last > THREE_MINUTES) {
                this.still();
            }
        }
        this.location.init(geoLocation);
    }

    private void movingSlow() {
        if (this.state != MOVING_SLOW) {
            this.stateTime = SystemClock.elapsedRealtime();
            this.state = MOVING_SLOW;
        }
        this.last = SystemClock.elapsedRealtime();
    }

    private void movingLittleFast() {
        if (this.state != MOVING_LITTLE_FAST) {
            if (this.state != MOVING_FAST)
                this.stateTime = SystemClock.elapsedRealtime();
            this.state = MOVING_LITTLE_FAST;
        }
        this.last = SystemClock.elapsedRealtime();
    }

    private void movingFast() {
        if (this.state != MOVING_FAST) {
            if (this.state != MOVING_LITTLE_FAST)
                this.stateTime = SystemClock.elapsedRealtime();
            this.state = MOVING_FAST;
        }
        this.last = SystemClock.elapsedRealtime();
    }

    private void still() {
        if (this.state != STILL) {
            this.stateTime = SystemClock.elapsedRealtime();
            this.state = STILL;
        }
        this.last = SystemClock.elapsedRealtime();
    }

}
