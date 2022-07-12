package ar.edu.unicen.isistan.asistan.storage.database.osm;

import androidx.room.Embedded;
import androidx.room.Entity;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;

@Entity(tableName = OSMPlace.TABLE_NAME)
public class OSMPlace extends OSM {

    public static final String TABLE_NAME = "osm_place";

    @Embedded
    private Coordinate location;

    public OSMPlace(String id, String name, int category, Coordinate location, boolean building) {
        super(id, name, category, building);
        this.location = location;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    @Override
    public void export(Place place) {
        place.setOsmId(this.getId());
        place.setName(this.getName());
        place.setArea(new Circle(this.getLocation(),30));
        place.setPlaceCategory(this.getCategory());
    }
}
