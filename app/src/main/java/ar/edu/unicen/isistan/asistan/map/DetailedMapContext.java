package ar.edu.unicen.isistan.asistan.map;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMArea;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;

public class DetailedMapContext extends MapContext {

    private static final int RADIUS = 400;

    @NotNull
    private ArrayList<Place> places; //estos serían los lugares personales
    @NotNull
    private ArrayList<OSMArea> osmAreas;
    @NotNull
    private final Coordinate center;

    public DetailedMapContext() {
        super();
        this.center = new Coordinate();
        this.places = new ArrayList<>();
        this.osmAreas = new ArrayList<>();
    }

    public OSMArea getOSMArea(@NotNull Coordinate location, double threshold) {
        OSMArea insideOf = null;

        ArrayList<OSMArea> aux = this.osmAreas;
        if (location.distance(this.center) >= RADIUS)
            aux = new ArrayList<>(Database.getInstance().openStreetMap().nearAreas(location, threshold));

        double minSurface = Double.MAX_VALUE;
        if (this.city != null) {
            for (OSMArea area : aux) {
                if (area.getArea().getSimplified().contains(location)) {
                    double surface = area.getArea().getSurface();
                    if (surface < minSurface) {
                        insideOf = area;
                        minSurface = surface;
                    }
                }
            }
        }

        if (insideOf == null) {
            double minDistance = threshold;
            minSurface = Double.MAX_VALUE;
            if (this.city != null) {
                for (OSMArea osmArea: aux) {
                    Area area = osmArea.getArea().getSimplified();
                    double distance = area.distance(location);
                    if (distance < minDistance) {
                        minDistance = distance;
                        minSurface = area.getSurface();
                        insideOf = osmArea;
                    } else if (distance == minDistance) {
                        double surface = area.getSurface();
                        if (surface < minSurface) {
                            insideOf = osmArea;
                            minSurface = surface;
                        }
                    }
                }
            }
        }

        return insideOf;
    }

    public OSMArea getOSMArea(@NotNull Coordinate location) {
        OSMArea insideOf = null;

        ArrayList<OSMArea> aux = this.osmAreas;
        if (location.distance(this.center) >= RADIUS)
            aux = new ArrayList<>(Database.getInstance().openStreetMap().nearAreas(location,0));

        double minSurface = Double.MAX_VALUE;
        if (this.city != null) {
            for (OSMArea area : aux) {
                if (area.getArea().getSimplified().contains(location)) {
                    double surface = area.getArea().getSurface();
                    if (surface < minSurface) {
                        insideOf = area;
                        minSurface = surface;
                    }
                }
            }
        }
        return insideOf;
    }

    public boolean inArea(@NotNull Coordinate location) {
        return this.getOSMArea(location) != null;
    }

    public Place getPlace(@NotNull Coordinate location) {
        Place insideOf = null;

        ArrayList<Place> aux = this.places;
        if (location.distance(this.center) >= RADIUS)
            aux = new ArrayList<>(Database.getInstance().mobility().near(location,50));

        double minSurface = Double.MAX_VALUE;
        for (Place place : aux) {
            if (place.getArea().getSimplified().contains(location)) {
                double surface = place.getArea().getSurface();
                if (surface < minSurface) {
                    insideOf = place;
                    minSurface = surface;
                }
            }
        }
        return insideOf;
    }

    public boolean inPlace(Coordinate location) {
        return this.getPlace(location) != null;
    }

    @Override
    public void update(@NotNull Coordinate location) {
        super.update(location);
        this.updateData(location);
    }

    private void updateData(@NotNull Coordinate location) {

        if (this.city != null) {
            //si esta detallada quiere decir que tengo los lugares
            if (!this.city.isDetailed()) {
                MapManager.getInstance().enqueueDownloadCity(this.city);
            } else if (this.center.isEmpty() || this.center.distance(location) > RADIUS) {
                //si está detallado cargo los cercanos a memoria en ese momento
                Database database = Database.getInstance();
                this.places = new ArrayList<>(database.mobility().allPlaces());
                this.osmAreas = new ArrayList<>(database.openStreetMap().nearAreas(location, RADIUS));
                this.center.init(location);
            }
        }
    }

    @Override
    public void notifyChanges() {
        super.notifyChanges();
        Coordinate auxLoc = new Coordinate(this.location);
        if (!auxLoc.isEmpty())
            this.updateData(auxLoc);
    }

}
