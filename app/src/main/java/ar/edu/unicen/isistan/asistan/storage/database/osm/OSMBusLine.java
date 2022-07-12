package ar.edu.unicen.isistan.asistan.storage.database.osm;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.events.Event;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;
import ar.edu.unicen.isistan.asistan.utils.geo.paths.Path;
import ar.edu.unicen.isistan.asistan.utils.geo.paths.PathsConverter;

@Entity(tableName = OSMBusLine.TABLE_NAME)
public class OSMBusLine {

    private static final double MAX_DISTANCE = 400;
    private static final double MAX_MATCH_DISTANCE = 30;
    private static final double INTERVAL = 50;
    private static final double MIN_MATCH_PERCENTAGE = 0.9;

    public final static String TABLE_NAME = "bus_line";

    @PrimaryKey
    private long id;
    @ColumnInfo(name="line")
    private String line;
    @ColumnInfo(name="paths")
    @TypeConverters(PathsConverter.class)
    private ArrayList<Path> paths;
    @Embedded
    private Bound bound;

    @Ignore
    public OSMBusLine(long id, @NotNull ArrayList<Path> paths) {
        this(id,paths,calculateBound(paths));
    }

    public OSMBusLine(long id, @NotNull ArrayList<Path> paths, Bound bound) {
        this.id = id;
        this.paths = paths;
        this.bound = bound;
    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Path> paths) {
        this.paths = paths;
    }

    public void addPath(Path path) {
        this.paths.add(path);
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public boolean matchEvents(ArrayList<Event> events) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Event event: events)
            coordinates.add(event.getLocation());
        return this.matchCoordinates(coordinates);
    }

    public boolean matchCoordinates(ArrayList<Coordinate> coordinates) {
        Coordinate last = null;
        int matched = 0;
        int total = 0;
        for (Coordinate coordinate: coordinates) {
            if (last == null || coordinate.distance(last) > INTERVAL) {
                last = coordinate;
                double distance = this.minDistance(last);
                if (distance > MAX_DISTANCE)
                    return false;
                if (distance < MAX_MATCH_DISTANCE)
                    matched++;
                total++;
            }
        }
        return (total != 0 && ( (double) matched/total) > MIN_MATCH_PERCENTAGE);
    }

    private double minDistance(Coordinate location) {
        double minDistance = Double.MAX_VALUE;
        for (Path path: this.paths) {
            double distance = path.distance(location);
            if (distance < minDistance)
                minDistance = distance;
        }
        return minDistance;
    }

    private static Bound calculateBound(ArrayList<Path> paths) {
        if (paths.isEmpty())
            return null;

        Bound bound = paths.get(0).getBound();
        for (int index = 1; index < paths.size(); index++)
            bound.increase(paths.get(index).getBound());
        return bound;
    }


}
