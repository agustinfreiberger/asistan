package ar.edu.unicen.isistan.asistan.storage.database.osm;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.AreaConverter;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.Circle;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

@Entity(tableName = OSMArea.TABLE_NAME)
public class OSMArea extends OSM {

    private static final double GREAT_SURFACE = 31400D;

    public static final String TABLE_NAME = "osm_area";

    @ColumnInfo(name="area")
    @TypeConverters(AreaConverter.class)
    private Area area;
    @Embedded
    private Bound bound;

    public OSMArea() {

    }

    @Ignore
    public OSMArea(String id, String name, int category, Area area, boolean building) {
        super(id, name, category, building);
        this.setArea(area);
        this.setBound(area.getBound());
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    @Override
    public void export(Place place) {
        place.setOsmId(this.getId());
        place.setName(this.getName());
        if (!this.isBuilding() && this.getArea().getSurface() > GREAT_SURFACE)
            place.setArea(new Circle(place.getArea().getCenter(),100));
        else
            place.setArea(this.getArea());
        place.setPlaceCategory(this.getCategory());
    }

}
