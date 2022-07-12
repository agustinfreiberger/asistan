package ar.edu.unicen.isistan.asistan.utils.geo.areas;

import java.util.List;
import androidx.annotation.Nullable;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public class Circle extends Area {

    private Coordinate center;
    private double radius;

    public Circle(Coordinate center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public double getSurface() {
        return Math.PI * Math.pow(this.radius,2);
    }

    @Override
    public Coordinate getCenter() {
        return this.center;
    }

    @Override
    public boolean contains(Coordinate location) {
        return this.center.distance(location) <= this.radius;
    }

    @Override
    public double distance(Coordinate coordinate) {
        return Math.max(0, this.center.distance(coordinate) - this.radius);
    }

    public void setCenter(Coordinate center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Circle) {
            Circle circle = (Circle) obj;
            return this.center.equals(circle.getCenter()) && this.radius == circle.getRadius();
        }
        return false;
    }

    @Override
    public Object map(MapController mapController) {
        return mapController.drawArea(this);
    }

    @Override
    public Circle copy() {
        return new Circle(this.center.copy(),this.radius);
    }

    @Override
    public Bound getBound() {
        return new Bound(this.center,this.radius);
    }

    @Override
    public Area getSimplified() {
        return this;
    }

    public static Circle makeCircle(Coordinate center, List<Coordinate> coordinates) {
        Circle circle = new Circle(center,0);

        double maxDistance = 0;
        double distance;
        for (Coordinate coordinate: coordinates) {
            distance = center.distance(coordinate);
            if (distance > maxDistance)
                maxDistance = distance;
        }

        circle.setRadius(maxDistance);
        return circle;
    }

}
