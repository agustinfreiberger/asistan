package ar.edu.unicen.isistan.asistan.tracker.mobility.state;

import android.location.LocationManager;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.map.MapContext;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.tracker.mobility.MobilityTracker;
import ar.edu.unicen.isistan.asistan.utils.queues.ReusableLimitedQueue;

public class TrackerState {

    private static final long TWO_AND_A_HALF_SECONDS = 2500L;
    private static final long TEN_SECONDS = 10000L;
    private static final long TWENTY_SECONDS = 20000L;
    private static final long THIRTY_SECONDS = 30000L;
    private static final long FORTY_SECONDS = 40000L;
    private static final long TWO_AND_A_HALF_MINUTES = 150000L;
    private static final long FIVE_MINUTES = 300000L;
    private static final long TEN_MINUTES = 600000L;
    private static final long TWENTY_MINUTES = 1200000L;
    private static final long FIFTEEN_SECONDS = 15000L;
    private static final long ONE_MINUTE = 60000L;
    private static final long EIGHT_HOURS= 28800000L;

    private static final double MAX_DISTANCE = 50D;
    private static final double MAX_FAST_VELOCITY = 55D; // 200 km/h
    private static final double MAX_LITTLE_FAST_VELOCITY = 20.625D; // 75 km/h
    private static final double MAX_SLOW_VELOCITY = 11D; // 40 km/h
    private static final double MAX_STILL_VELOCITY = 4D; // 15 km/h

    private boolean inForeground;
    private boolean usingGps;

    private boolean airPlaneMode;
    private long startTime;
    private long lastModification;

    private long gpsInteraction;
    private long netInterval;
    private long gpsInterval;
    private short provider;
    private long lastGps;
    private long lastNetwork;
    private boolean lastValid;
    private long nextThreshold;

    @NonNull
    private final transient Database database;
    @NonNull
    private final MobilityState activityState;
    @NonNull
    private final GeoLocation lastValidLocation;
    @NonNull
    private final GeoLocation lastTrusted;
    @NonNull
    private final ReusableLimitedQueue<GeoLocation> buffer;
    @NonNull
    private MapContext mapContext;

    public TrackerState() {
        this.activityState = new MobilityState();
        this.lastValidLocation = new GeoLocation();
        this.lastTrusted = new GeoLocation();
        this.buffer = new ReusableLimitedQueue<>(3);
        this.database = Database.getInstance();
        this.mapContext = new MapContext();
        init();
    }

    public void init() {
        this.netInterval = 0;
        this.gpsInterval = 0;
        this.lastGps = 0;
        this.lastNetwork = 0;
        this.activityState.init();
        this.provider = MobilityTracker.NONE;
        this.lastValidLocation.init();
        this.lastTrusted.init();
        this.buffer.clear();
        this.startTime = SystemClock.elapsedRealtime();
        this.lastModification = System.currentTimeMillis();
        this.gpsInteraction = 0;
        this.lastValid = true;
        this.inForeground = false;
        this.airPlaneMode = false;
        this.mapContext.init();
        this.usingGps = false;
        this.nextThreshold = 0;
    }

    public boolean inRuralArea() {
        return this.mapContext.inRuralArea();
    }

    // Update map with a new location
    private void updateMap(@NonNull GeoLocation location) {
        this.mapContext.update(location.getCoordinate());
        if (this.mapContext.inRuralArea()) {
            location.setTrusted(false);
            this.startTravel();
        }
    }

    // Update last location time
    private void updateTimes(@NonNull GeoLocation location) {
        if (LocationManager.GPS_PROVIDER.equals(location.getProvider()))
            this.lastGps = location.getLocTime();
        else if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider()))
            this.lastNetwork = location.getLocTime();
    }

    public long getNextThreshold() {
        return nextThreshold;
    }

    public void updateNextThreshold() {
        long interval = this.calculateGpsThreshold();
        long max = Math.max(this.getLastValidLocation().getElapsedTime(),this.getStateTime());
        long nextThreshold = max + interval;

        if (this.nextThreshold == 0 || this.nextThreshold > nextThreshold || this.nextThreshold < this.lastValidLocation.getElapsedTime())
            this.nextThreshold = nextThreshold;
    }

    // Get last network location time
    public long getLastNetwork() {
        return this.lastNetwork;
    }

    // Get last gps location time
    public long getLastGps() {
        return this.lastGps;
    }

    // Update state with a new location and return a list of tursted locations
    public ArrayList<GeoLocation> update(@NonNull GeoLocation location) {
        ArrayList<GeoLocation> trusteds = new ArrayList<>();
        this.updateMap(location);
        this.updateTimes(location);
        if (this.isNext(location)) {
            this.addBuffer(location, trusteds);
        } else if (location.isTrusted()) {
            location.setTrusted(false);
            this.database.geoLocation().update(location);
        }
        return trusteds;
    }

    // If location is trusted and newer than last one
    private boolean isNext(@NonNull GeoLocation location) {
        if (!location.isTrusted())
            return false;
        if (this.lastValidLocation.isEmpty())
            return true;
        else
            return (this.lastValidLocation.getTime() < location.getTime());
    }

    // Add location to buffer to verify if it is valid and trusted
    private void addBuffer(@NonNull GeoLocation location, ArrayList<GeoLocation> trusteds) {
        if (this.valid(location)) {
            this.lastValidLocation.init(location);
            this.lastValid = true;
            if (this.buffer.isEmpty()) {
                location.setTrusted(false);
                this.database.geoLocation().update(location);
                this.buffer.add(location);
            } else if (this.buffer.size() == 1) {
                GeoLocation buffered = this.buffer.get(0);
                double distance = buffered.distance(location);
                if (distance >= MAX_DISTANCE) {
                    location.setTrusted(false);
                    this.database.geoLocation().update(location);
                } else {
                    if (!buffered.isTrusted()) {
                        this.trust(buffered);
                        trusteds.add(buffered);
                    }
                    trusteds.add(location);
                    this.updateTrusted(location);
                    this.buffer.clear();
                }
                this.buffer.add(location);
            } else if (this.buffer.size() == 2) {
                GeoLocation buffered = this.buffer.get(0);
                GeoLocation problematic = this.buffer.get(1);
                double distanceBuffered = buffered.distance(location);
                double distanceProblematic = problematic.distance(location);
                double distanceInter = buffered.distance(problematic);

                this.trust(buffered);
                trusteds.add(buffered);
                if (distanceBuffered < distanceInter && distanceBuffered < distanceProblematic) {
                    this.buffer.remove(problematic);
                } else {
                    this.trust(problematic);
                    trusteds.add(problematic);
                    this.buffer.remove(buffered);
                }

                this.addBuffer(location, trusteds);
            }
        } else {
            this.lastValid = false;
            location.setTrusted(false);
            this.database.geoLocation().update(location);
        }
    }

    // Start to travel (rural area)
    public void startTravel() {
        this.lastValidLocation.init();
        this.lastTrusted.init();
    }

    public void setInForeground(boolean inForeground) {
        this.inForeground = inForeground;
    }

    public boolean isInForeground() {
        return this.inForeground;
    }

    public boolean isUsingGps() {
        return usingGps;
    }

    public void setUsingGps(boolean usingGps) {
        this.usingGps = usingGps;
    }

    // Check if location is valid (valid location may be trusted in the future)
    private boolean valid(@NonNull GeoLocation location) {
        if (this.lastTrusted.isEmpty())
            return true;

        double distance = location.distance(this.lastTrusted);
        long time = (location.getTime() - this.lastTrusted.getTime()) / 1000;
        double velocity = distance / time;

        double maxVelocity;
        switch (this.activityState.getState()) {
            case MobilityState.MOVING_LITTLE_FAST:
                maxVelocity = MAX_LITTLE_FAST_VELOCITY;
                break;
            case MobilityState.MOVING_SLOW:
                maxVelocity = MAX_SLOW_VELOCITY;
                break;
            case MobilityState.STILL:
                maxVelocity = MAX_STILL_VELOCITY;
                break;
            default:
                maxVelocity = MAX_FAST_VELOCITY;
                break;
        }

        return (velocity < maxVelocity);
    }

    // Mark a location as trusted
    private void trust(@NonNull GeoLocation location) {
        if (!location.isTrusted()) {
            location.setTrusted(true);
            this.database.geoLocation().update(location);
            this.updateTrusted(location);
        }
    }

    // Report a new trusted location
    private void updateTrusted(@NonNull GeoLocation location) {
        this.activityState.update(location);
        this.lastTrusted.init(location);
    }

    // Report a new activity
    public void update(@NonNull Activity activity) {
        this.activityState.update(activity);
    }

    public int getState() {
        return this.activityState.getState();
    }

    public long getStateTime() {
        return this.activityState.getStateTime();
    }

    public void setAirPlaneMode(boolean airPlaneMode) {
        this.airPlaneMode = airPlaneMode;
    }

    public boolean isAirPlaneMode() {
        return this.airPlaneMode;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public boolean isGpsEnabled() {
        return this.provider == MobilityTracker.ONLY_GPS || this.provider == MobilityTracker.BOTH;
    }

    public boolean isNetworkEnabled() {
        return this.provider == MobilityTracker.ONLY_NETWORK || this.provider == MobilityTracker.BOTH;
    }

    // Should prioritize GPS over network
    public boolean prioritizeGps() {
        return GeoLocation.GPS_PROVIDER.equals(this.lastValidLocation.getProvider()) && (this.getState() == MobilityState.MOVING_LITTLE_FAST  || this.getState() == MobilityState.MOVING_FAST) && ((SystemClock.elapsedRealtime() - this.lastValidLocation.getElapsedTime()) < TEN_SECONDS);
    }

    // Get Network pending intent interval
    public long getNetInterval() {
        return this.netInterval;
    }

    // Set Network pending intent interval
    public void setNetInterval(long netInterval) {
        this.netInterval = netInterval;
    }

    // Get GPS pending intent interval
    public long getGpsInterval() {
        return this.gpsInterval;
    }

    // Set GPS pending intent interval
    public void setGpsInterval(long gpsInterval) {
        this.gpsInterval = gpsInterval;
    }

    // New provider
    public void setProvider(short provider) {
        this.provider = provider;
    }

    // Current provider
    public short getProvider() {
        return this.provider;
    }

    // Set last time the GPS was enabled or disabled
    public void setGpsInteraction(long time) {
        this.gpsInteraction = time;
    }

    // Get last trusted location
    @NonNull
    public GeoLocation getLastTrusted() {
        return this.lastTrusted;
    }

    // Get last valid location
    @NonNull
    public GeoLocation getLastValidLocation() {
        return this.lastValidLocation;
    }

    // Check if last accurate location was also valid (not necessary trusted)
    public boolean isLastValid() {
        return this.lastValid;
    }

    // Update modification time (util to know when the state is old)
    public void modify() {
        this.lastModification = System.currentTimeMillis();
    }

    // Network interval for pending intent
    public long calculateNetInterval() {
        if (this.lastTrusted.isEmpty())
            return TWENTY_SECONDS;

        if (this.inRuralArea())
            return TWO_AND_A_HALF_MINUTES;

        int state = this.getState();
        if (state == MobilityState.MOVING_FAST)  {
            // Moving fast
            return TWENTY_SECONDS;
        } else if (state == MobilityState.MOVING_LITTLE_FAST) {
            // Maybe moving fast
            return THIRTY_SECONDS;
        } else if (state == MobilityState.STILL) {
            // Still
            long stateTime = this.getStateTime();
            GeoLocation lastTrusted = this.getLastTrusted();
            if (!lastTrusted.isEmpty()) {
                long sinceState = lastTrusted.getElapsedTime() - stateTime;
                long sinceAccurate = SystemClock.elapsedRealtime() - lastTrusted.getElapsedTime();
                if (sinceState > TWENTY_MINUTES && sinceAccurate < TWENTY_MINUTES)
                    return TEN_MINUTES;
                else if (sinceState > TEN_MINUTES && sinceAccurate < TEN_MINUTES)
                    return FIVE_MINUTES;
                else if (sinceState > FIVE_MINUTES && sinceAccurate < FIVE_MINUTES)
                    return TWO_AND_A_HALF_MINUTES;
                else
                    return FORTY_SECONDS;
            } else
                return FORTY_SECONDS;
        } else {
            // Moving slow or unexpected case
            return FORTY_SECONDS;
        }
    }

    // GPS interval for pending intent
    public long calculateGpsInterval() {
        return TWO_AND_A_HALF_SECONDS;
    }

    // Calculate time to wait for activating GPS again
    public long calculateGpsThreshold() {
        int state = this.getState();

        if (this.inRuralArea() || (state != MobilityState.MOVING_FAST && state != MobilityState.MOVING_LITTLE_FAST))
            return this.calculateNetInterval() * 2;

        return state == MobilityState.MOVING_FAST ? TEN_SECONDS : FIFTEEN_SECONDS;
    }

    // Check if shoud wait for using GPS
    public boolean waitForGps() {
        long currentTime = SystemClock.elapsedRealtime();

        long gpsThreshold = this.calculateGpsThreshold();

        return ((currentTime - this.lastValidLocation.getElapsedTime()) <= gpsThreshold) ||
                (this.isGpsEnabled() && ((currentTime - this.gpsInteraction) >= THIRTY_SECONDS)) ||
                (!this.isGpsEnabled() && ((currentTime - this.gpsInteraction) <= gpsThreshold));
    }

    // Check if this state is old
    public boolean isOld() {
        // If the mobile was rebooted
        if (this.getStartTime() > SystemClock.elapsedRealtime())
            return true;

        long diff = System.currentTimeMillis() - this.lastModification;

        // If tracker was in foreground
        if (this.inForeground)
            return diff > ONE_MINUTE;

        return diff > EIGHT_HOURS;
    }

}
