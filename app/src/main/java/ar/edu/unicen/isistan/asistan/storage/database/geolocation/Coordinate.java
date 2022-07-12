package ar.edu.unicen.isistan.asistan.storage.database.geolocation;

import androidx.room.Ignore;

import com.google.android.gms.maps.model.LatLng;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;
import org.jetbrains.annotations.NotNull;
import org.osmdroid.util.GeoPoint;

import ar.edu.unicen.isistan.asistan.utils.queues.Reusable;

public class Coordinate implements Reusable<Coordinate> {

    public static final int DEGREE_TO_METERS = 111319;
    public static final int MAX_LAT = 90;
    public static final int MIN_LAT = -90;
    public static final int MAX_LNG = 180;
    public static final int MIN_LNG = -180;

    private double latitude;
    private double longitude;

    public Coordinate() {
        this.init();
    }

    public Coordinate(Coordinate coordinate) {
        this.init(coordinate);
    }

    @Ignore
    public Coordinate(double latitude, double longitude) {
        this.init(latitude,longitude);
    }

    @Override
    public void init() {
        this.latitude = 0;
        this.longitude = 0;
    }

    public void init(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void init(@NotNull Coordinate coordinate) {
        this.latitude = coordinate.latitude;
        this.longitude = coordinate.longitude;
    }

    @Override
    public boolean isEmpty() {
        return this.latitude == 0 && this.longitude == 0;
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

    @NotNull
    public Coordinate copy() {
        Coordinate coordinate = new Coordinate();
        coordinate.init(this);
        return coordinate;
    }

    public double distance(@NotNull Coordinate coordinate) {
        GeodesicData data = Geodesic.WGS84.Inverse(this.getLatitude(), this.getLongitude(), coordinate.getLatitude(), coordinate.getLongitude(), GeodesicMask.DISTANCE);
        return data.s12;
    }

    public boolean equals(Object object) {
        if (object == this)
            return true;
        if (object instanceof Coordinate) {
            Coordinate coordinate = (Coordinate) object;
            return this.getLatitude() == coordinate.getLatitude() && this.getLongitude() == coordinate.getLongitude();
        } else {
            return false;
        }
    }

    @Override
    @NotNull
    public String toString() {
        return this.getLatitude() + ", " + this.getLongitude();
    }

    @NotNull
    public LatLng toLatLng() {
        return new LatLng(this.latitude,this.longitude);
    }

    @NotNull
    public GeoPoint toGeoPoint() {
        return new GeoPoint(this.latitude,this.longitude);
    }

    @NotNull
    public Coordinate middle(@NotNull Coordinate coordinate) {
        return new Coordinate((this.latitude + coordinate.getLatitude())/2, (this.longitude + coordinate.getLongitude())/2);
    }


}
