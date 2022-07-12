package ar.edu.unicen.isistan.asistan.tracker.mobility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.gson.Gson;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.application.notifications.NotificationManager;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.activity.Activity;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.GeoLocation;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.tracker.Tracker;
import ar.edu.unicen.isistan.asistan.tracker.mobility.receivers.ActivityReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.receivers.AirPlaneReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.receivers.GpsLocationReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.receivers.NetworkLocationReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.receivers.PassiveLocationReceiver;
import ar.edu.unicen.isistan.asistan.tracker.mobility.state.MobilityState;
import ar.edu.unicen.isistan.asistan.tracker.mobility.state.TrackerState;
import ar.edu.unicen.isistan.asistan.tracker.statemachine.StateMachineTracker;
import ar.edu.unicen.isistan.asistan.views.asistan.MainActivity;

public class MobilityTracker {

    private static final String CLASS_TAG = "MobilityTracker";

    private static final String GPS_ENABLED = "GPS_ENABLED";
    private static final String GPS_DISABLED = "GPS_DISABLED";
    private static final String NETWORK_ENABLED = "NETWORK_ENABLED";
    private static final String NETWORK_DISABLED = "NETWORK_DISABLED";

    private static final String NOTIFICATION_PERMISSION_ID = "ar.edu.unicen.isistan.ayacucho-tracker.mobility-paused_due_permission";
    private static final String NOTIFICATION_LOCATION_ID = "ar.edu.unicen.isistan.ayacucho-tracker.mobility-paused_due_location";

    private static final String PREFERENCES = "ar.edu.unicen.isistan.asistan-tracker.mobility";
    private static final String STATE_KEY = "state";

    private static final long FIVE_SECONDS = 5000L;
    private final static long TEN_SECONDS = 10000;
    private static final long FIVE_MINUTES = 300000L;
    private static final long TEN_MINUTES = 600000L;
    private static final float ACCURACY_THRESHOLD = 60F;
    private static final float SECOND_ACCURACY_THRESHOLD = 100F;
    private static final float MAX_ALTITUDE = 5200F;
    private static final float MIN_ALTITUDE = -450F;

    public static final short ONLY_NETWORK = 1;
    public static final short ONLY_GPS = 2;
    public static final short BOTH = 3;
    public static final short NONE = 4;

    // TrackerState instance is keept during each app instance
    @androidx.annotation.Nullable
    private static TrackerState trackerStateInstance;
    // LocationManager a instance is keept during each app instance
    @androidx.annotation.Nullable
    private static LocationManager locationManager;
    // PendingIntent instances are keept during each app instance
    @androidx.annotation.Nullable
    private static PendingIntent passiveLocPendingIntent;
    @androidx.annotation.Nullable
    private static PendingIntent netLocPendingIntent;
    @androidx.annotation.Nullable
    private static PendingIntent gpsLocPendingIntent;
    @androidx.annotation.Nullable
    private static PendingIntent activityPendingIntent;
    // Broadcast receiver for airplane mode changes
    @Nullable
    private static AirPlaneReceiver airplaneModeReceiver;

    // Reusable objects
    @NonNull
    private static final Activity activity;
    @NonNull
    private static final GeoLocation geoLocation;

    static {
        activity = new Activity();
        geoLocation = new GeoLocation();
    }

    // Private methods to load, save and clean trackerState / airplaneModeReceiver

    @NonNull
    private static TrackerState load(@NonNull Context context) {
        if (MobilityTracker.trackerStateInstance == null) {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String json = preferences.getString(STATE_KEY, null);

            try {
                MobilityTracker.trackerStateInstance = new Gson().fromJson(json, TrackerState.class);
                if (MobilityTracker.trackerStateInstance.isOld())
                    MobilityTracker.trackerStateInstance.init();
            } catch (Exception e) {
                e.printStackTrace();
                MobilityTracker.trackerStateInstance = new TrackerState();
            }
        }

        return MobilityTracker.trackerStateInstance;
    }

    private static void save(@NonNull Context context, @NonNull TrackerState trackerState) {
        trackerState.modify();
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(STATE_KEY,new Gson().toJson(trackerState));
        editor.apply();
    }

    private static void clean() {
        MobilityTracker.trackerStateInstance = null;
        MobilityTracker.airplaneModeReceiver = null;
        MobilityTracker.locationManager = null;
        MobilityTracker.passiveLocPendingIntent = null;
        MobilityTracker.netLocPendingIntent = null;
        MobilityTracker.gpsLocPendingIntent = null;
        MobilityTracker.activityPendingIntent = null;
        StateMachineTracker.clean();
    }

    private static LocationManager getLocationManager(@NonNull Context context) {
        if (MobilityTracker.locationManager == null)
            MobilityTracker.locationManager =  (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return MobilityTracker.locationManager;
    }

    @NonNull
    private static PendingIntent getPassiveLocationPendingIntent(@NonNull Context context) {
        if (MobilityTracker.passiveLocPendingIntent == null) {
            Intent locationIntent = new Intent(context.getApplicationContext(), PassiveLocationReceiver.class);
            MobilityTracker.passiveLocPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return MobilityTracker.passiveLocPendingIntent;
    }

    @NonNull
    private static PendingIntent getGpsLocationPendingIntent(@NonNull Context context) {
        if (MobilityTracker.gpsLocPendingIntent == null) {
            Intent locationIntent = new Intent(context.getApplicationContext(), GpsLocationReceiver.class);
            MobilityTracker.gpsLocPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return MobilityTracker.gpsLocPendingIntent;
    }

    @NonNull
    private static PendingIntent getNetworkLocationPendingIntent(@NonNull Context context) {
        if (MobilityTracker.netLocPendingIntent == null) {
            Intent locationIntent = new Intent(context.getApplicationContext(), NetworkLocationReceiver.class);
            MobilityTracker.netLocPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return MobilityTracker.netLocPendingIntent;
    }

    @NonNull
    private static PendingIntent getActivityPendingIntent(@NonNull Context context) {
        if (MobilityTracker.activityPendingIntent == null) {
            Intent activityIntent = new Intent(context.getApplicationContext(), ActivityReceiver.class);
            MobilityTracker.activityPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return MobilityTracker.activityPendingIntent;
    }

    // Private methods when a new Geolocation is received

    private static boolean enoughAccuracy(@NonNull TrackerState trackerState) {
        if (MobilityTracker.geoLocation.getAltitude() < MIN_ALTITUDE || MobilityTracker.geoLocation.getAltitude() > MAX_ALTITUDE)
            return false;

        if (MobilityTracker.geoLocation.getAccuracy() <= ACCURACY_THRESHOLD)
            return true;

        if (MobilityTracker.geoLocation.getAccuracy() >= SECOND_ACCURACY_THRESHOLD)
            return false;

        GeoLocation lastAccurate = trackerState.getLastValidLocation();
        return !lastAccurate.isEmpty() && lastAccurate.distance(MobilityTracker.geoLocation) <= (ACCURACY_THRESHOLD/2);
    }

    private static void updateLocation(@NonNull Context context, @NonNull TrackerState trackerState) {
        Database.getInstance().geoLocation().insert(MobilityTracker.geoLocation);

        ArrayList<GeoLocation> trustedLocations = trackerState.update(MobilityTracker.geoLocation);
        if (trackerState.inRuralArea()) {
            StateMachineTracker.startTravelling(context);
            disableActivity(context);
        } else {
            enableActivity(context, trackerState);
        }

        update(context, trackerState);
        sendEvent(context, trustedLocations);
    }

    private static void sendEvent(@NonNull Context context, @NonNull ArrayList<GeoLocation> trustedLocations) {
        Event event = new Event();
        Database database = Database.getInstance();
        for (GeoLocation trustedLocation: trustedLocations) {
            Activity activity = database.activity().lastBefore(trustedLocation.getLocTime());
            event.init(activity,trustedLocation);
            StateMachineTracker.process(context, event);
        }
    }

    // Update pending intents according and save modified trackerState

    private static void update(@NonNull Context context, @NonNull TrackerState trackerState) {
        // Location is disabled, stop everything
        if (!isLocationEnabled(context)) {
            requestLocationEnable(context);
        } else if (!Tracker.hasRequiredPermissions(context)) {
            requestLocationPermission(context);
        } else {
            enablePassive(context);
            enableActivity(context, trackerState);
            updateProviders(context, trackerState);
            updateRequests(context, trackerState);
            checkForeground(context, trackerState);
            save(context, trackerState);
        }
    }

    // Update provider (enable or disable network and gps)
    private static void updateProviders(@NonNull Context context, @NonNull TrackerState trackerState) {
        short provider = calculateProvider(context, trackerState);

        switch (provider) {
            case ONLY_GPS:
                disableNetwork(context, trackerState);
                enableGps(context, trackerState);
                break;
            case BOTH:
                enableNetwork(context, trackerState);
                enableGps(context, trackerState);
                break;
            case NONE:
                disableNetwork(context, trackerState);
                disableGps(context, trackerState);
                break;
            case ONLY_NETWORK:
            default:
                disableGps(context, trackerState);
                enableNetwork(context, trackerState);
                break;
        }

        trackerState.setProvider(provider);
    }

    public static boolean isLocationEnabled(@NonNull Context context) {
        context = context.getApplicationContext();

        LocationManager locationManager = getLocationManager(context);

        if (locationManager == null)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return (locationManager.isLocationEnabled());
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    // Calculate which providers should be enabled (none, network, gps, or both)
    private static short calculateProvider(@NonNull Context context, @NonNull TrackerState trackerState) {
        // Update next threshold (this should be executed always first)
        trackerState.updateNextThreshold();

        // Airplane mode is activated
        if (trackerState.isAirPlaneMode())
            return NONE;

        // I am in rural area
        if (trackerState.inRuralArea())
            return ONLY_NETWORK;

        LocationManager locationManager = getLocationManager(context);

        // GPS is not available
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return ONLY_NETWORK;

        boolean waitForGPS = trackerState.waitForGps();
        int state = trackerState.getState();

        // I am moving fast
        if (state == MobilityState.MOVING_LITTLE_FAST)
            return waitForGPS ? ONLY_NETWORK : BOTH;
        else if (state == MobilityState.MOVING_FAST)
            return waitForGPS ? NONE : ONLY_GPS;

        // I should wait for using GPS again
        if (waitForGPS)
            return ONLY_NETWORK;

        // If last accurate location was not valid
        if (!trackerState.isLastValid())
            return BOTH;

        // If I received a valid location recently, I stop GPS
        return SystemClock.elapsedRealtime() < trackerState.getNextThreshold() ? ONLY_NETWORK : BOTH;
    }

    // Update pending intents with new parameters if it is required
    private static void updateRequests(@NonNull Context context, @NonNull TrackerState trackerState) {
        try {
            LocationManager locationManager = getLocationManager(context);
            if (locationManager != null) {
                if (trackerState.isNetworkEnabled()) {
                    long netInterval = trackerState.calculateNetInterval();
                    if (trackerState.getNetInterval() != netInterval) {
                        PendingIntent networkLocationPendingIntent = getNetworkLocationPendingIntent(context);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, netInterval, 0, networkLocationPendingIntent);
                        trackerState.setNetInterval(netInterval);
                    }
                }

                if (trackerState.isGpsEnabled()) {
                    long gpsInterval = trackerState.calculateGpsInterval();
                    if (trackerState.getGpsInterval() != gpsInterval) {
                        PendingIntent gpsLocationPendingIntent = getGpsLocationPendingIntent(context);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsInterval, 0, gpsLocationPendingIntent);
                        trackerState.setUsingGps(true);
                        trackerState.setGpsInterval(gpsInterval);
                    }
                }
            }
        } catch (SecurityException e) {
            requestLocationPermission(context);
        }
    }

    // Private methods to enable/disable gps

    private static void enableGps(@NonNull Context context, @NonNull TrackerState trackerState) {
        try {
            LocationManager locationManager = getLocationManager(context);
            if (locationManager != null && !trackerState.isAirPlaneMode()) {
                long gpsInterval = trackerState.calculateGpsInterval();
                if (gpsInterval != trackerState.getGpsInterval()) {
                    PendingIntent locationPendingIntent = getGpsLocationPendingIntent(context);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsInterval, 0, locationPendingIntent);
                    trackerState.setGpsInterval(gpsInterval);
                    if (!trackerState.isUsingGps()) {
                        trackerState.setUsingGps(true);
                        trackerState.setGpsInteraction(SystemClock.elapsedRealtime());
                        Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, GPS_ENABLED));
                    }
                }
            }
        } catch (SecurityException e) {
            requestLocationPermission(context);
        }
    }

    private static void disableGps(@NonNull Context context, @NonNull TrackerState trackerState) {
        LocationManager locationManager = getLocationManager(context);
        if (locationManager != null) {
            PendingIntent locationPendingIntent = getGpsLocationPendingIntent(context);
            locationManager.removeUpdates(locationPendingIntent);
            if (trackerState.isUsingGps()) {
                trackerState.setUsingGps(false);
                trackerState.setGpsInteraction(SystemClock.elapsedRealtime());
                trackerState.setGpsInterval(0L);
                Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, GPS_DISABLED));
            }
        }
    }

    // Private methods to enable/disable network

    private static void enableNetwork(@NonNull Context context, @NonNull TrackerState trackerState) {
        try {
            LocationManager locationManager = getLocationManager(context);
            if (locationManager != null && !trackerState.isAirPlaneMode()) {
                long netInterval = trackerState.calculateNetInterval();
                if (netInterval != trackerState.getNetInterval()) {
                    PendingIntent locationPendingIntent = getNetworkLocationPendingIntent(context);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, netInterval, 0, locationPendingIntent);
                    trackerState.setNetInterval(netInterval);
                    Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, NETWORK_ENABLED));
                }
            }
        } catch (SecurityException e) {
            requestLocationPermission(context);
        }
    }

    private static void disableNetwork(@NonNull Context context, @NonNull TrackerState trackerState) {
        LocationManager locationManager = getLocationManager(context);
        if (locationManager != null) {
            PendingIntent locationPendingIntent = getNetworkLocationPendingIntent(context);
            locationManager.removeUpdates(locationPendingIntent);
            if (trackerState.getNetInterval() != 0L) {
                trackerState.setNetInterval(0L);
                Database.getInstance().asistan().insert(new AsistanEvent(CLASS_TAG, NETWORK_DISABLED));
            }
        }
    }

    // Private methods to enable/disable passive receiver

    private static void enablePassive(@NonNull Context context) {
        try {
            LocationManager locationManager = getLocationManager(context);
            if (locationManager != null) {
                PendingIntent locationPendingIntent = getPassiveLocationPendingIntent(context);
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationPendingIntent);
            }
        } catch (SecurityException e) {
            requestLocationPermission(context);
        }

    }

    private static void disablePassive(@NonNull Context context) {
        LocationManager locationManager = getLocationManager(context);
        if (locationManager != null) {
            PendingIntent locationPendingIntent = getPassiveLocationPendingIntent(context);
            locationManager.removeUpdates(locationPendingIntent);
        }
    }

    // Private methods to enable/disable activity

    private static void enableActivity(@NonNull Context context, @NonNull TrackerState trackerState) {
        ActivityRecognitionClient activityClient = ActivityRecognition.getClient(context.getApplicationContext());
        PendingIntent activityPendingIntent = getActivityPendingIntent(context);
        if (trackerState.inRuralArea() || trackerState.isAirPlaneMode())
            activityClient.requestActivityUpdates(FIVE_MINUTES, activityPendingIntent);
        else
            activityClient.requestActivityUpdates(TEN_SECONDS, activityPendingIntent);
    }

    private static void disableActivity(@NonNull Context context) {
        ActivityRecognitionClient activityClient = ActivityRecognition.getClient(context.getApplicationContext());
        Intent activityIntent = new Intent(context.getApplicationContext(), ActivityReceiver.class);
        PendingIntent activityPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        activityClient.removeActivityUpdates(activityPendingIntent);
    }

    // Private methods to enable/disable foreground service or check if it is needed

    private static void enableForeground(@NonNull Context context, @NonNull TrackerState trackerState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ForegroundService.startService(context);
            if (!trackerState.isInForeground())
                ForegroundService.createMinimizedNotification(context);
            trackerState.setInForeground(true);
        }
    }

    private static void disableForeground(@NonNull Context context, @NonNull TrackerState trackerState) {
        ForegroundService.stopService(context);
        trackerState.setInForeground(false);
    }

    private static void checkForeground(@NonNull Context context, @NonNull TrackerState trackerState) {
        if ((trackerState.isNetworkEnabled() && trackerState.getNetInterval() < TEN_MINUTES) || (trackerState.isGpsEnabled() && trackerState.getGpsInterval() < TEN_MINUTES) || (trackerState.getProvider() == MobilityTracker.NONE && !trackerState.isAirPlaneMode()))
            enableForeground(context, trackerState);
        else
            disableForeground(context, trackerState);
    }

    // Private methods to register/unregister airplane mode listener

    private static void registerAirPlaneReceiver(@NonNull Context context, @NonNull TrackerState trackerState) {
        // Obtain current airplane mode
        boolean airplaneMode;
        airplaneMode = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        trackerState.setAirPlaneMode(airplaneMode);

        // Register receiver
        if (MobilityTracker.airplaneModeReceiver == null)
            MobilityTracker.airplaneModeReceiver = new AirPlaneReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        context.registerReceiver(MobilityTracker.airplaneModeReceiver, filter);
    }

    private static void unregisterAirPlaneReceiver(@NonNull Context context) {
        if (MobilityTracker.airplaneModeReceiver != null) {
            try {
                context.unregisterReceiver(MobilityTracker.airplaneModeReceiver);
            } catch (Exception ignored) { }
        }
    }

    // Request location permisions if needed

    private static void requestLocationPermission(@NonNull Context context) {
        NotificationManager notificationManager = NotificationManager.getInstance(context);
        NotificationCompat.Builder builder = notificationManager.getNotification(context.getString(R.string.permission_required), context.getString(R.string.permission_required_explain), NotificationManager.ERRORS_CHANNEL_ID, NotificationCompat.PRIORITY_HIGH, true);
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_PERMISSION_ID.hashCode(),builder);
        Tracker.stop(context);
    }

    private static void requestLocationEnable(@NonNull Context context) {
        NotificationManager notificationManager = NotificationManager.getInstance(context);
        NotificationCompat.Builder builder = notificationManager.getNotification(context.getString(R.string.location_disabled), context.getString(R.string.location_disabled_explain), NotificationManager.ERRORS_CHANNEL_ID, NotificationCompat.PRIORITY_HIGH, true);
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_LOCATION_ID.hashCode(),builder);
        Tracker.stop(context);
    }

    // Updaters to be used in receivers

    public synchronized static void updateActivity(@NonNull Context context, @NonNull ActivityRecognitionResult result) {
        context = context.getApplicationContext();

        MobilityTracker.activity.init(result);
        Database.getInstance().activity().insert(MobilityTracker.activity);

        TrackerState state = load(context);
        state.update(MobilityTracker.activity);
        update(context, state);
    }

    public synchronized static void updateNetwork(@NonNull Context context, @NonNull Location location) {
        context = context.getApplicationContext();

        geoLocation.init(location);

        TrackerState trackerState = load(context);
        if (geoLocation.getLocElapsedTime() < (trackerState.getStartTime() - FIVE_MINUTES))
            return;

        if ((geoLocation.getLocTime() - trackerState.getLastNetwork()) < FIVE_SECONDS)
            return;

        if (enoughAccuracy(trackerState) && !trackerState.prioritizeGps())
            geoLocation.setTrusted(true);

        updateLocation(context, trackerState);
    }

    public synchronized static void updateGps(@NonNull Context context, @NonNull Location location) {
        context = context.getApplicationContext();

        geoLocation.init(location);

        TrackerState trackerState = load(context);

        if (geoLocation.getLocElapsedTime() < (trackerState.getStartTime() - FIVE_MINUTES))
            return;

        if (!trackerState.isGpsEnabled() && (geoLocation.getLocTime() - trackerState.getLastGps() < FIVE_SECONDS))
            return;

        if (enoughAccuracy(trackerState))
            geoLocation.setTrusted(true);

        updateLocation(context, trackerState);
    }

    public synchronized static void updateAirPlaneMode(@NonNull Context context, boolean airPlaneMode) {
        context = context.getApplicationContext();

        TrackerState trackerState = load(context);
        trackerState.setAirPlaneMode(airPlaneMode);
        update(context, trackerState);
    }

    // Start and stop methods

    public synchronized static void start(@NonNull Context context) {
        context = context.getApplicationContext();

        // Load TrackerState
        TrackerState trackerState = load(context);

        save(context,trackerState);

        registerAirPlaneReceiver(context,trackerState);

        // Init pending intents if conditions are ok
        if (!trackerState.isAirPlaneMode()) {
            enablePassive(context);
            if (!trackerState.inRuralArea())
                enableActivity(context,trackerState);
            update(context, trackerState);
        } else {
            save(context, trackerState);
        }

    }

    public synchronized static void stop(@NonNull Context context) {
        context = context.getApplicationContext();

        disableAll(context);

        TrackerState trackerState = load(context);
        disableForeground(context,trackerState);
        trackerState.init();

        save(context,trackerState);

        clean();
    }

    private static void disableAll(@NonNull Context context) {
        TrackerState trackerState = load(context);
        unregisterAirPlaneReceiver(context);
        disablePassive(context);
        disableActivity(context);
        disableNetwork(context, trackerState);
        disableGps(context, trackerState);
    }

    private static void refresh(@NonNull Context context) {
        disableAll(context);
        start(context);
    }

    public synchronized static void bootCompleted(@NonNull Context context) {
        context = context.getApplicationContext();
        refresh(context);
    }

    public synchronized static void replacedPackage(@NonNull Context context) {
        context = context.getApplicationContext();
        refresh(context);
    }

    public synchronized static void appOpened(@NonNull Context context) {
        context = context.getApplicationContext();

        if (MobilityTracker.trackerStateInstance == null) {
            TrackerState trackerState = load(context);
            trackerState.setNetInterval(0);
            trackerState.setGpsInterval(0);
            start(context);
        }
    }

    public synchronized static void mapUpdate(@NonNull Context context) {
        context = context.getApplicationContext();

        TrackerState trackerState = load(context);
        update(context, trackerState);
    }

    public static synchronized void checkForRefresh(@NonNull Context context) {
        context = context.getApplicationContext();

        TrackerState trackerState = load(context);

        long gpsThreshold = trackerState.calculateGpsThreshold();
        long lastLocation = Math.max(trackerState.getLastGps(), trackerState.getLastNetwork());
        long now = System.currentTimeMillis();

        if (now - lastLocation > gpsThreshold * 5)
            refresh(context);
    }

}
