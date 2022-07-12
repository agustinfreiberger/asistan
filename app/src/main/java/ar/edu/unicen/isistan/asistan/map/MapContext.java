package ar.edu.unicen.isistan.asistan.map;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMCity;

public class MapContext {

    private static final String CLASS_TAG = "MapContext";
    private static final String LOADING = "LOADING";
    private static final String LOAD_COMPLETE = "LOAD_COMPLETE";
    private static final int LOAD_RADIUS = 15000;

    @Nullable
    protected OSMCity city;
    @NotNull
    protected final Coordinate location;

    public MapContext() {
        this.location = new Coordinate();
        this.city = null;
        MapManager.getInstance().addListener(this);
    }

    public void init() {
        this.location.init();
        this.city = null;
    }

    public void init(@Nullable MapContext mapContext) {
        if (mapContext == null) {
            this.init();
        } else {
            this.location.init(mapContext.location);
            this.city = mapContext.city;
        }
    }

    private OSMCity getCity(@NotNull Coordinate location) {
        Database database = Database.getInstance();
        database.asistan().insert(new AsistanEvent(CLASS_TAG,LOADING));

        GeodesicData data = Geodesic.WGS84.Inverse(location.getLatitude(), location.getLongitude(), location.getLatitude(), location.getLongitude()+1, GeodesicMask.DISTANCE);
        double longDegreeToMeters = data.s12;
        double latDegrees = (double) LOAD_RADIUS / (double) Coordinate.DEGREE_TO_METERS;
        double longDegrees = (double) LOAD_RADIUS / longDegreeToMeters;
        double south = Math.max(location.getLatitude() - latDegrees, Coordinate.MIN_LAT);
        double west = Math.max(location.getLongitude() - longDegrees, Coordinate.MIN_LNG);
        double north = Math.min(location.getLatitude() + latDegrees, Coordinate.MAX_LAT);
        double east = Math.min(location.getLongitude() + longDegrees, Coordinate.MAX_LNG);

        List<OSMCity> cities = database.openStreetMap().cities(south, west, north, east);

        OSMCity insideOf = null;
        for (OSMCity city : cities)
            if (city.contains(location) && (insideOf == null || insideOf.distance(location) > city.distance(location)))
                insideOf = city;

        database.asistan().insert(new AsistanEvent(CLASS_TAG,LOAD_COMPLETE));
        return insideOf;
    }

    public boolean inCity() {
        return this.city != null;
    }

    public boolean inRuralArea() {
        return !this.inCity();
    }

    public void update(@NotNull Coordinate location) {
        OSMCity beforeUpdate = this.city;
        OSMCity afterUpdate = beforeUpdate;
        this.location.init(location);

        if (beforeUpdate != null && !beforeUpdate.contains(location))
            afterUpdate = null;

        if (afterUpdate == null)
            afterUpdate = this.getCity(location);

        if (afterUpdate == null)
            MapManager.getInstance().checkDownload(location);

        if (afterUpdate == null || !afterUpdate.equals(beforeUpdate)) {
            this.city = afterUpdate;
        }
    }

    public void notifyChanges() {
        OSMCity beforeUpdate = this.city;
        Coordinate auxLoc = new Coordinate(this.location);
        if (!auxLoc.isEmpty() && (beforeUpdate == null || !beforeUpdate.isDetailed())) {
            OSMCity afterUpdate = this.getCity(auxLoc);
            if (afterUpdate != null && !afterUpdate.equals(beforeUpdate))
                this.city = afterUpdate;
        }
    }

}
