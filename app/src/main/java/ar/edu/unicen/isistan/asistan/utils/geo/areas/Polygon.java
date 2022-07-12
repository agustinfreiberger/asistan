package ar.edu.unicen.isistan.asistan.utils.geo.areas;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.PolygonArea;
import net.sf.geographiclib.PolygonResult;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;
import ar.edu.unicen.isistan.asistan.utils.geo.simplifiers.DouglasPeucker;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public class Polygon extends Area {

    private ArrayList<Coordinate> coordinates;
    private Polygon simplified;

    public Polygon() {
        this.coordinates = new ArrayList<>();
    }

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public double getSurface() {
        PolygonArea area = new PolygonArea(Geodesic.WGS84,false);
        for(Coordinate coordinate: this.coordinates)
            area.AddPoint(coordinate.getLatitude(), coordinate.getLongitude());
        PolygonResult result = area.Compute();
        return Math.abs(result.area);
    }

    @Override
    public Coordinate getCenter() {
        double minLat = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double minLong = Integer.MAX_VALUE;
        double maxLong = Integer.MIN_VALUE;

        for (Coordinate coordinate: this.getCoordinates()) {
            if (coordinate.getLatitude() < minLat)
                minLat = coordinate.getLatitude();
            if (coordinate.getLatitude() > maxLat)
                maxLat = coordinate.getLatitude();
            if (coordinate.getLongitude() < minLong)
                minLong = coordinate.getLongitude();
            if (coordinate.getLongitude() > maxLong)
                maxLong = coordinate.getLongitude();
        }

        Coordinate leftTop = new Coordinate(maxLat, minLong);
        Coordinate rightBottom = new Coordinate(minLat, maxLong);
        return leftTop.middle(rightBottom);
    }

    @Override
    public boolean contains(Coordinate location) {
        boolean contains = false;
        for (int i = 0, j = this.coordinates.size()-1; i < this.coordinates.size(); j = i++) {
            if ( ((this.coordinates.get(i).getLatitude() > location.getLatitude()) != (this.coordinates.get(j).getLatitude() > location.getLatitude())) && (location.getLongitude() < (this.coordinates.get(j).getLongitude()-this.coordinates.get(i).getLongitude()) * (location.getLatitude()-this.coordinates.get(i).getLatitude()) / (this.coordinates.get(j).getLatitude()-this.coordinates.get(i).getLatitude()) + this.coordinates.get(i).getLongitude()) )
                contains = !contains;
        }
        return contains;
    }

    @Override
    public double distance(Coordinate coordinate) {
        if (this.contains(coordinate))
            return 0;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Polygon) {
            Polygon polygon = (Polygon) obj;
            return this.coordinates.equals(polygon.getCoordinates());
        }
        return false;
    }

    @Override
    public Object map(MapController mapController) {
        return mapController.drawArea(this);
    }

    @Override
    public Polygon copy() {
        Polygon polygon = new Polygon();
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Coordinate coordinate: this.coordinates)
            coordinates.add(coordinate.copy());
        polygon.setCoordinates(coordinates);
        if (this.simplified != null)
            polygon.setSimplified(this.simplified.copy());
        return polygon;
    }

    @Override
    public Bound getBound() {
        return new Bound(this.getCoordinates());
    }

    private void simplify() {
        DouglasPeucker peucker = new DouglasPeucker(5);
        ArrayList<Coordinate> coordinates = peucker.simplify(this.coordinates);
        Polygon polygon = new Polygon();
        polygon.setCoordinates(coordinates);
        this.setSimplified(polygon);
    }

    public void setSimplified(Polygon polygon) {
        this.simplified = polygon;
    }

    @Override
    public Polygon getSimplified() {
        if (this.simplified == null)
            this.simplify();
        return this.simplified;
    }

}
