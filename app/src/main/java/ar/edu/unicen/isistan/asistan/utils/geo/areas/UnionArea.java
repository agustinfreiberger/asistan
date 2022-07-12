package ar.edu.unicen.isistan.asistan.utils.geo.areas;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.Nullable;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;
import ar.edu.unicen.isistan.asistan.views.map.MapController;

public class UnionArea extends Area {

    private ArrayList<Area> areas;
    private UnionArea simplified;

    public UnionArea() {
        this.areas = new ArrayList<>();
    }

    public UnionArea(Area... areas) {
        this.areas = new ArrayList<>(Arrays.asList(areas));
    }

    public ArrayList<Area> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<Area> areas) {
        this.areas = areas;
    }

    public void addSurface(Area area) {
        this.areas.add(area);
    }

    @Override
    public double getSurface() {
        // TODO CORREGIR
        double maxSurface = 0;
        for (Area area : this.areas) {
            double surface = area.getSurface();
            if (maxSurface < surface)
                maxSurface = surface;
        }
        return maxSurface;
    }

    @Override
    public Coordinate getCenter() {
        double lat = 0;
        double lng = 0;
        double totalSurface = 0;

        double[] surfaces = new double[this.areas.size()];

        for (int index = 0; index < this.areas.size(); index++) {
            Area area = this.areas.get(index);
            surfaces[index] = area.getSurface();
            totalSurface += surfaces[index];
        }

        for (int index = 0; index < this.areas.size(); index++) {
            Area area = this.areas.get(index);
            Coordinate center = area.getCenter();
            double factor = (surfaces[index]/totalSurface);
            lat += center.getLatitude() * factor;
            lng += center.getLongitude() * factor;
        }

        return new Coordinate(lat,lng);
    }

    @Override
    public boolean contains(Coordinate location) {
        for (Area area : this.areas)
            if (area.contains(location))
                return true;
        return false;
    }

    @Override
    public double distance(Coordinate coordinate) {
        double minDistance = Double.MAX_VALUE;

        for (Area area: this.areas) {
            double distance = area.distance(coordinate);
            if (minDistance > distance)
                minDistance = distance;
            if (minDistance == 0)
                return 0;
        }

        return minDistance;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof UnionArea) {
            UnionArea union = (UnionArea) obj;
            return this.areas.equals(union.getAreas());
        }
        return false;
    }

    @Override
    public Object map(MapController mapController) {
        return mapController.drawArea(this);
    }

    @Override
    public UnionArea copy() {
        UnionArea unionArea = new UnionArea();
        ArrayList<Area> areas = new ArrayList<>();
        for (Area area: this.areas)
            areas.add(area.copy());
        unionArea.setAreas(areas);
        if (this.simplified != null)
            unionArea.setSimplified(this.simplified.copy());
        return unionArea;
    }

    @Override
    public Bound getBound() {
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
        UnionArea area = new UnionArea();
        area.setAreas(areas);
        this.setSimplified(area);
    }

    public void setSimplified(UnionArea area) {
        this.simplified = area;
    }

    @Override
    public UnionArea getSimplified() {
        if (this.simplified == null)
            this.simplify();
        return this.simplified;
    }

}
