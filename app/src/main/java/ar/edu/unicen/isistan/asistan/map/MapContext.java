package ar.edu.unicen.isistan.asistan.map;

import com.google.api.client.json.Json;
import com.google.gson.JsonObject;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ar.edu.unicen.isistan.asistan.storage.database.Database;
import ar.edu.unicen.isistan.asistan.storage.database.asistan.AsistanEvent;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMCity;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMDao;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMDao_Impl;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OSMPlace;
import ar.edu.unicen.isistan.asistan.storage.database.osm.OverpassAPI;

public class MapContext {

    private static final String CLASS_TAG = "MapContext";
    private static final String LOADING = "LOADING";
    private static final String LOAD_COMPLETE = "LOAD_COMPLETE";
    private static final int LOAD_RADIUS = 15000;
    private static final String TANDIL_AMENITIES_QUERY = "[timeout:30][out:json]; (node['amenity'~'pub|cafe|restaurant|theatre|cinema'](-37.39525510959719,-59.25750732421875,-37.21918303359261,-59.02130126953124);); out body;";
    private static final String TANDIL_TOURISM_QUERY = "[timeout:30][out:json]; (node['tourism'~'attraction|gallery|zoo'](-37.39525510959719,-59.25750732421875,-37.21918303359261,-59.02130126953124);); out body;";

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
        //this.insertPOIS();
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

    public void insertPOIS(){
        //ACA INSERTA LOS POIS DE TANDIL
        Database database = Database.getInstance();
        OSMDao osmDao = new OSMDao_Impl(database);

        //VER DE MANDARLE EL {BOX} OBTENIDO DE LA LOCALIZACIÓN EN VEZ DE POR CONST
        ArrayList<JsonObject> tourism_attractions_tandil = new ArrayList<>(OverpassAPI.getPois(TANDIL_TOURISM_QUERY));
        ArrayList<JsonObject> tourism_amenities_tandil = new ArrayList<>(OverpassAPI.getPois(TANDIL_AMENITIES_QUERY));

        for (JsonObject attraction:tourism_attractions_tandil) {
            Coordinate coord = new Coordinate();
            coord.setLatitude(attraction.get("lat").getAsDouble());
            coord.setLongitude(attraction.get("lon").getAsDouble());
            JsonObject tags = attraction.get("tags").getAsJsonObject();
            OSMPlace osmPlace = new OSMPlace(attraction.get("id").getAsString(), tags.get("name").getAsString(), PlaceCategory.valueOf(tags.get("tourism").getAsString().toUpperCase(Locale.ROOT)).getCode(), coord, false);
            osmDao.insert(osmPlace);
        }
        for (JsonObject attraction:tourism_amenities_tandil) {
            Coordinate coord = new Coordinate();
            coord.setLatitude(attraction.get("lat").getAsDouble());
            coord.setLongitude(attraction.get("lon").getAsDouble());
            JsonObject tags = attraction.get("tags").getAsJsonObject();
            OSMPlace osmPlace = new OSMPlace(attraction.get("id").getAsString(), tags.get("name").getAsString(), PlaceCategory.valueOf(tags.get("amenity").getAsString().toUpperCase(Locale.ROOT)).getCode(), coord, false);
            osmDao.insert(osmPlace);
        }

    }
}
