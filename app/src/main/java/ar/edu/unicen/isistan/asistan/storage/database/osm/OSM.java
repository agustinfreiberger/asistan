package ar.edu.unicen.isistan.asistan.storage.database.osm;

import com.google.gson.annotations.JsonAdapter;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Place;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

@JsonAdapter(OSMDAdapter.class)
public abstract class OSM {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    @NotNull
    private String id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "category")
    private int category;
    @ColumnInfo(name = "building")
    private boolean building;
    @Ignore
    private OSM.OSMType type;

    public enum OSMType {

        PLACE(OSMPlace.class), AREA(OSMArea.class);

        private Class<? extends OSM> type;

        OSMType(Class<? extends OSM> type) {
            this.type = type;
        }

        public Class<? extends OSM> getOSMClass() {
            return this.type;
        }

        public static OSM.OSMType getType(Class<? extends OSM> osmClass) {
            for (OSM.OSMType type: OSM.OSMType.values()) {
                if (type.getOSMClass().equals(osmClass))
                    return type;
            }
            return null;
        }

    }

    public OSM() {
        this(null);
    }

    @Ignore
    public OSM(String id) {
        this(id,null,PlaceCategory.UNSPECIFIED.getCode(),false);
    }

    @Ignore
    public OSM(@NotNull String id, String name, int category, boolean building) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.building = building;
        this.type = OSM.OSMType.getType(this.getClass());
    }

    public OSMType getType() {
        return type;
    }

    public void setType(OSMType type) {
        this.type = type;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public abstract void export(Place place);

    @Override
    @NotNull
    public String toString() {
        return this.getId();
    }

}
