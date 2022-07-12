package ar.edu.unicen.isistan.asistan.storage.database.osm;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;

@Entity(tableName = OSMCity.TABLE_NAME)
public class OSMCity {

    public static final String TABLE_NAME = "osm_city";

    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "name")
    private String name;
    @Embedded
    private Coordinate coordinate;
    @ColumnInfo(name = "radius")
    private int radius;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "detailed")
    private boolean detailed;

    public OSMCity(long id) {
        this.id = id;
        this.name = null;
        this.detailed = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean contains(@NotNull Coordinate location) {
        return this.distance(location) <= this.radius;
    }

    public double distance(@NotNull Coordinate location) {
        return this.getCoordinate().distance(location);
    }

    public boolean isDetailed() {
        return detailed;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OSMCity osmCity = (OSMCity) o;
        return this.id == osmCity.id && this.detailed == osmCity.detailed;
    }

}
