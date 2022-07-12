package ar.edu.unicen.isistan.asistan.storage.database.geolocation;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import com.google.android.gms.location.FusedLocationProviderClient;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ar.edu.unicen.isistan.asistan.utils.queues.Reusable;

@Entity(tableName = GeoLocation.TABLE_NAME)
public class GeoLocation implements Reusable<GeoLocation> {

    public static final String TABLE_NAME = "geolocation";

    private final static int NO_DATA = -1;
    public final static String REPEATER_PROVIDER = "repeater";
    public final static String NETWORK_PROVIDER = "network";
    public final static String GPS_PROVIDER = "gps";
    public final static String FUSED_PROVIDER = "fused";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "provider")
    private String provider;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "location_time")
    private long locTime;
    @ColumnInfo(name = "elapsed_time")
    private long elapsedTime;
    @ColumnInfo(name = "location_elapsed_time")
    private long locElapsedTime;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;
    @ColumnInfo(name = "altitude")
    private double altitude;
    @ColumnInfo(name = "speed")
    private float speed;
    @ColumnInfo(name = "bearing")
    private float bearing;
    @ColumnInfo(name = "accuracy")
    private float accuracy;
    @ColumnInfo(name = "speed_accuracy")
    private float speedAccuracy;
    @ColumnInfo(name = "vertical_accuracy")
    private float verticalAccuracy;
    @ColumnInfo(name = "bearing_accuracy")
    private float bearingAccuracy;
    @ColumnInfo(name = "trusted")
    private boolean trusted;

    public GeoLocation() {
        this.init();
    }

    public void init() {
        this.id = 0;
        this.provider = null;
        this.time = NO_DATA;
        this.latitude = NO_DATA;
        this.longitude = NO_DATA;
        this.accuracy = NO_DATA;
        this.speed = NO_DATA;
        this.altitude = NO_DATA;
        this.bearing = NO_DATA;
        this.speedAccuracy = NO_DATA;
        this.verticalAccuracy = NO_DATA;
        this.bearingAccuracy = NO_DATA;
        this.elapsedTime = NO_DATA;
        this.locElapsedTime = NO_DATA;
        this.locTime = NO_DATA;
        this.trusted = false;
    }

    public GeoLocation(@NotNull Location location) {
        this();
        init(location);
    }

    public void init(@NotNull Location location) {
        this.init();
        this.provider = location.getProvider();
        this.time = System.currentTimeMillis();
        this.elapsedTime = SystemClock.elapsedRealtime();
        this.locTime = location.getTime();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        if (location.hasAltitude())
            this.altitude = location.getAltitude();
        if (location.hasSpeed())
            this.speed = location.getSpeed();
        if (location.hasBearing())
            this.bearing = location.getBearing();
        if (location.hasAccuracy())
            this.accuracy = location.getAccuracy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.locElapsedTime = location.getElapsedRealtimeNanos() / 1000000;
        } else {
            this.locElapsedTime = NO_DATA;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (location.hasVerticalAccuracy())
                this.verticalAccuracy = location.getVerticalAccuracyMeters();
            if (location.hasSpeedAccuracy())
                this.speedAccuracy = location.getSpeedAccuracyMetersPerSecond();
            if (location.hasBearingAccuracy())
                this.bearingAccuracy = location.getBearingAccuracyDegrees();
        } else {
            if (location.getExtras() != null)
                this.verticalAccuracy = location.getExtras().getFloat(FusedLocationProviderClient.KEY_VERTICAL_ACCURACY,-1);
        }

    }

    public void init(@Nullable GeoLocation geoLocation) {
        if (geoLocation != null) {
            this.id = geoLocation.getId();
            this.provider = geoLocation.getProvider();
            this.time = geoLocation.getTime();
            this.latitude = geoLocation.getLatitude();
            this.longitude = geoLocation.getLongitude();
            this.accuracy = geoLocation.getAccuracy();
            this.speed = geoLocation.getSpeed();
            this.altitude = geoLocation.getAltitude();
            this.bearing = geoLocation.getBearing();
            this.speedAccuracy = geoLocation.getSpeedAccuracy();
            this.verticalAccuracy = geoLocation.getVerticalAccuracy();
            this.bearingAccuracy = geoLocation.getBearingAccuracy();
            this.elapsedTime = geoLocation.getElapsedTime();
            this.locElapsedTime = geoLocation.getLocElapsedTime();
            this.locTime = geoLocation.getLocTime();
            this.trusted = geoLocation.isTrusted();
        } else {
            this.init();
        }
    }

    public boolean isEmpty() {
        return this.time == NO_DATA;
    }

    @NotNull
    public GeoLocation copy() {
        GeoLocation location = new GeoLocation();
        location.init(this);
        return location;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeedAccuracy() {
        return speedAccuracy;
    }

    public void setSpeedAccuracy(float speedAccuracy) {
        this.speedAccuracy = speedAccuracy;
    }

    public float getVerticalAccuracy() {
        return verticalAccuracy;
    }

    public void setVerticalAccuracy(float verticalAccuracy) {
        this.verticalAccuracy = verticalAccuracy;
    }

    public float getBearingAccuracy() {
        return bearingAccuracy;
    }

    public void setBearingAccuracy(float bearingAccuracy) {
        this.bearingAccuracy = bearingAccuracy;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getLocElapsedTime() {
        return locElapsedTime;
    }

    public void setLocElapsedTime(long locElapsedTime) {
        this.locElapsedTime = locElapsedTime;
    }

    public long getLocTime() {
        return locTime;
    }

    public void setLocTime(long locTime) {
        this.locTime = locTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @NotNull
    public Coordinate getCoordinate() {
        return new Coordinate(this.latitude,this.longitude);
    }

    public boolean isTrusted() {
        return trusted;
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }

    public double distance(@NotNull GeoLocation geoLocation) {
        GeodesicData data = Geodesic.WGS84.Inverse(this.getLatitude(), this.getLongitude(), geoLocation.getLatitude(), geoLocation.getLongitude(), GeodesicMask.DISTANCE);
        return data.s12;
    }

    @NotNull
    public String toString() {
        return this.id + ", " + this.provider + ": {" + this.latitude + ", " + this.longitude + "}, acc: "+ this.accuracy + ", { time:" + this.getTime() + ", loc_time: " + this.getLocTime() + ", elapsed_loc_time: " + this.getLocElapsedTime() + "}, trusted: " + this.isTrusted();
    }


}
