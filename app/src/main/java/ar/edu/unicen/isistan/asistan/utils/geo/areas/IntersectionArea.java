package ar.edu.unicen.isistan.asistan.utils.geo.areas;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.Nullable;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public class IntersectionArea extends Area {

    private ArrayList<Area> areas;
    private IntersectionArea simplified;

    public IntersectionArea() {
        this.areas = new ArrayList<>();
    }

    public IntersectionArea(Area... areas) {
        this.areas = new ArrayList<>(Arrays.asList(areas));
    }

    public void setAreas(ArrayList<Area> areas) {
        this.areas = areas;
    }

    public ArrayList<Area> getAreas() {
        return this.areas;
    }

    @Override
    public double getSurface() {
        // TODO CORREGIR
        double minSurface = Double.MAX_VALUE;
        for (Area area : this.areas) {
            double surface = area.getSurface();
            if (minSurface > surface)
                minSurface = surface;
        }
        return minSurface;
    }

    @Override
    public Coordinate getCenter() {
        // TODO IMPLEMENTAR
        return null;
    }

    @Override
    public boolean contains(Coordinate location) {
        for (Area area: this.areas) {
            if (!area.contains(location))
                return false;
        }
        return true;
    }

    @Override
    public double distance(Coordinate coordinate) {
        double maxDistance = 0;

        for (Area area: this.areas) {
            double distance = area.distance(coordinate);
            if (distance > maxDistance)
                maxDistance = distance;
        }

        return maxDistance;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof IntersectionArea) {
            IntersectionArea intersection = (IntersectionArea) obj;
            return this.areas.equals(intersection.getAreas());
        }
        return false;
    }

    @Override
    public Object map(MapController mapController) {
        return mapController.drawArea(this);
    }

    @Override
    public IntersectionArea copy() {
        IntersectionArea intersectionArea = new IntersectionArea();
        ArrayList<Area> areas = new ArrayList<>();
        for (Area area: this.areas)
            areas.add(area.copy());
        intersectionArea.setAreas(areas);
        if (this.simplified != null)
            intersectionArea.setSimplified(this.simplified.copy());
        return intersectionArea;
    }

    @Override
    public Bound getBound() {
        // TODO CORREGIR PARA QUE
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Area area: this.getAreas()) {
            Bound bound = area.getBound();
            coordinates.add(bound.getNorthEast());
            coordinates.add(bound.getSouthWest());
        }
        return new Bound(coordinates);
    }

    private void simplify() {
        ArrayList<Area> areas = new ArrayList<>();
        for (Area area: this.areas)
            areas.add(area.getSimplified());
        IntersectionArea area = new IntersectionArea();
        area.setAreas(areas);
        this.setSimplified(area);
    }

    public void setSimplified(IntersectionArea area) {
        this.simplified = area;
    }

    @Override
    public IntersectionArea getSimplified() {
        if (this.simplified == null)
            this.simplify();
        return this.simplified;
    }

}
