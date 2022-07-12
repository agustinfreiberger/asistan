package ar.edu.unicen.isistan.asistan.storage.database.mobility.places;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ar.edu.unicen.isistan.asistan.utils.geo.areas.Area;
import ar.edu.unicen.isistan.asistan.utils.geo.areas.AreaConverter;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.visits.Visit;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

@Entity(tableName = Place.TABLE_NAME)
public class Place {

    public static final String TABLE_NAME = "place";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "upload")
    private boolean upload;
    @ColumnInfo(name="name")
    private String name;
    @ColumnInfo(name="description")
    private String description;
    @ColumnInfo(name="place_category")
    private int placeCategory;
    @ColumnInfo(name="fixed_location")
    private boolean fixedLocation;
    @ColumnInfo(name="osm_id")
    private String osmId;
    @ColumnInfo(name="area")
    @TypeConverters(AreaConverter.class)
    private Area area;
    @Ignore
    private transient List<Visit> visits;
    @Embedded
    private Bound bound;

    public Place() {
        this.id = 0;
        this.name = null;
        this.upload = false;
        this.placeCategory = PlaceCategory.NEW.getCode();
        this.fixedLocation = false;
        this.area = null;
        this.osmId = null;
        this.bound = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean hasId() {
        return this.id != 0;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public String getName() {
        return name;
    }

    @NotNull
    public String getShowName() {
        return (this.name == null) ? "¿Lugar nuevo?" : this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPlaceCategory() {
        return placeCategory;
    }

    public void setPlaceCategory(int placeCategory) {
        this.placeCategory = placeCategory;
    }

    public boolean isFixedLocation() {
        return fixedLocation;
    }

    public void setFixedLocation(boolean fixedLocation) {
        this.fixedLocation = fixedLocation;
    }

    public String getOsmId() {
        return osmId;
    }

    public void setOsmId(String osmId) {
        this.osmId = osmId;
    }

    public Bound getBound() {
        if (this.bound == null)
            this.bound = this.area.getBound();
        return new Bound(this.bound);
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        if (area != null) {
            this.area = area.copy();
            this.bound = this.area.getBound();
        } else {
            this.area = null;
        }
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public void load(Place place) {
        this.setId(place.getId());
        this.setUpload(place.isUpload());
        this.setName(place.getName());
        this.setDescription(place.getDescription());
        this.setPlaceCategory(place.getPlaceCategory());
        this.setFixedLocation(place.isFixedLocation());
        this.setArea(place.getArea().copy());
        this.setOsmId(place.getOsmId());
        this.setUpload(place.isUpload());
        this.setVisits(place.getVisits());
    }

    public boolean different(Place place) {
        return !((this.getId() == place.getId()) &&
                ((this.name != null && this.name.equals(place.getName())) || (this.name == null && place.getName() == null)) &&
                ((this.description != null && this.description.equals(place.getDescription())) || (this.description == null && place.getDescription() == null)) &&
                (this.placeCategory == place.getPlaceCategory()) &&
                ((this.osmId != null && this.osmId.equals(place.getOsmId())) || (this.osmId == null && place.getOsmId() == null)) &&
                this.getArea().equals(place.getArea()));
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (o instanceof Place) {
            Place place = (Place) o;
            return (this.hasId() && place.hasId() && this.getId() == place.getId());
        }

        return false;
    }

    public Place obfuscate() {
        Place place = new Place();
        place.setName(this.getName());
        place.setPlaceCategory(this.getPlaceCategory());
        place.setOsmId(this.getOsmId());
        return place;
    }
}
