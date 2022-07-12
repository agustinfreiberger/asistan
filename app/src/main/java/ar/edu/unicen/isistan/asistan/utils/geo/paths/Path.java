package ar.edu.unicen.isistan.asistan.utils.geo.paths;

import android.util.Log;

import java.util.ArrayList;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

public class Path {

    private ArrayList<Coordinate> coordinates;

    public Path() {
        this.coordinates = new ArrayList<>();
    }

    public Path(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public void addCoordinate(Coordinate coordinate) {
        if (this.coordinates.isEmpty() || !this.coordinates.get(this.coordinates.size()-1).equals(coordinate))
            this.coordinates.add(coordinate);
    }

    public Bound getBound() {
        return new Bound(this.coordinates);
    }

    public double distance(Coordinate coordinate) {
        double minDist = Double.MAX_VALUE;

        for (int index = 0; index < this.coordinates.size(); index++) {
            Coordinate c1 = this.coordinates.get(index);
            Coordinate c2 = index < this.coordinates.size()-1 ? this.coordinates.get(index+1) : this.coordinates.get(0);

            double deltaLat = c2.getLatitude() - c1.getLatitude();
            double deltaLng = c2.getLongitude() - c1.getLongitude();

            if (deltaLat == 0 && deltaLng == 0)
                return c1.distance(coordinate);

            double u = (((coordinate.getLatitude() - c1.getLatitude()) * deltaLat) + ((coordinate.getLongitude() - c1.getLongitude()) * deltaLng)) / (deltaLat * deltaLat + deltaLng * deltaLng);

            Coordinate nearest;
            if (u < 0)
                nearest = c1;
            else if (u > 1)
                nearest = c2;
            else
                nearest = new Coordinate(c1.getLatitude() + u * deltaLat,c1.getLongitude() + u * deltaLng);

            double distance = coordinate.distance(nearest);
            if (minDist > distance) {
                minDist = distance;
            }
        }

        return minDist;
    }

}
