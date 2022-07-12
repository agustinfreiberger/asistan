package ar.edu.unicen.isistan.asistan.utils.geo.bound;

import androidx.room.Ignore;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;

public class Bound {

    private double north;
    private double east;
    private double south;
    private double west;

    public Bound() {

    }

    @Ignore
    public Bound(Bound bound) {
        this.north = bound.getNorth();
        this.east = bound.getEast();
        this.south = bound.getSouth();
        this.west = bound.getWest();
    }

    @Ignore
    public Bound(double north, double south, double east, double west) {
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    @Ignore
    public Bound(@NotNull Coordinate location, double distance) {
        GeodesicData data = Geodesic.WGS84.Inverse(location.getLatitude(), location.getLongitude() - 0.5, location.getLatitude(), location.getLongitude() + 0.5, GeodesicMask.DISTANCE);
        double deltaMeters = data.s12;
        double deltaLong = distance / deltaMeters;
        double deltaLat = distance / Coordinate.DEGREE_TO_METERS;

        Coordinate northEast = new Coordinate(location.getLatitude() + deltaLat, location.getLongitude() + deltaLong);
        Coordinate southWest = new Coordinate(location.getLatitude() - deltaLat, location.getLongitude() - deltaLong);

        this.north = northEast.getLatitude();
        this.east = northEast.getLongitude();
        this.south = southWest.getLatitude();
        this.west = southWest.getLongitude();
    }

    @Ignore
    public Bound(@NotNull ArrayList<Coordinate> coordinates) {

        double north = -Double.MAX_VALUE;
        double south = Double.MAX_VALUE;
        double east = -Double.MAX_VALUE;
        double west = Double.MAX_VALUE;

        for (Coordinate coordinate: coordinates) {
            if (coordinate.getLatitude() > north)
                north = coordinate.getLatitude();
            if (coordinate.getLatitude() < south)
                south = coordinate.getLatitude();
            if (coordinate.getLongitude() > east)
                east = coordinate.getLongitude();
            if (coordinate.getLongitude() < west)
                west = coordinate.getLongitude();
        }

        Coordinate northEast = new Coordinate(north, east);
        Coordinate southWest = new Coordinate(south, west);

        this.north = northEast.getLatitude();
        this.east = northEast.getLongitude();
        this.south = southWest.getLatitude();
        this.west = southWest.getLongitude();
    }

    public double getNorth() {
        return this.north;
    }

    public void setNorth(double north) {
        this.north = north;
    }

    public double getSouth() {
        return this.south;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public double getEast() {
        return this.east;
    }

    public void setEast(double east) {
        this.east = east;
    }

    public double getWest() {
        return this.west;
    }

    public void setWest(double west) {
        this.west = west;
    }

    @NotNull
    public Coordinate getSouthWest() {
        return new Coordinate(this.south,this.west);
    }

    @NotNull
    public Coordinate getNorthEast() {
        return new Coordinate(this.north,this.east);
    }

    public void increase(float percentage) {
        double deltaLat = Math.abs(this.north-this.south) * percentage;
        double deltaLng = Math.abs(this.east-this.west) * percentage;
        this.south -= deltaLat;
        this.north += deltaLat;
        this.east += deltaLng;
        this.west -= deltaLng;
    }

    public void increase(@NotNull Bound bound) {
        this.south = Math.min(this.south,bound.getSouth());
        this.north = Math.max(this.north,bound.getNorth());
        this.east = Math.max(this.east,bound.getEast());
        this.west = Math.min(this.west,bound.getWest());
    }

     public Coordinate getCenter() {
        return new Coordinate((this.north + this.south)/2, (this.west + this.east)/2);
    }
}

