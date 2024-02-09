package ar.edu.unicen.isistan.asistan.storage.database.osm;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import ar.edu.unicen.isistan.asistan.storage.database.geolocation.Coordinate;
import ar.edu.unicen.isistan.asistan.utils.geo.bound.Bound;

@Dao
public abstract class OSMDao {

    /* OSM */

    public ArrayList<OSM> near(Coordinate location, double maxDistance) {
        ArrayList<OSM> out = new ArrayList<>();
        out.addAll(this.nearPlaces(location,maxDistance));
        out.addAll(this.nearAreas(location,maxDistance));
        return out;
    }

    /* OSMPlaces*/

    @Query("SELECT * FROM " + OSMPlace.TABLE_NAME + " WHERE (latitude BETWEEN :south AND :north) AND (longitude BETWEEN :west AND :east)")
    public abstract List<OSMPlace> allPlaces(double north, double south, double east, double west);

    public List<OSMPlace> nearPlaces(Coordinate location, double maxDistance) {
        Bound bound = new Bound(location,maxDistance);
        return this.allPlaces(bound.getNorth(), bound.getSouth(), bound.getEast(), bound.getWest());
    }

    @Query("SELECT * FROM " + OSMPlace.TABLE_NAME + " WHERE _id IS :id")
    public abstract OSMPlace select(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(OSMPlace place);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insert(OSMPlace... places);

    @Update
    public abstract int update(OSMPlace place);

    @Delete
    public abstract int delete(OSMPlace place);

    /* OSMAreas */

    @Query("SELECT * FROM " + OSMArea.TABLE_NAME + " WHERE NOT((west > :east) OR (:west > east) OR (south > :north) OR (:south > north)) AND name IS NOT null")
    public abstract List<OSMArea> allAreas(double north, double south, double east, double west);

    public List<OSMArea> nearAreas(Coordinate location, double maxDistance) {
        Bound bound = new Bound(location,maxDistance);
        return this.allAreas(bound.getNorth(), bound.getSouth(), bound.getEast(), bound.getWest());
    }



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(OSMArea area);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insert(OSMArea... areas);

    @Update
    public abstract int update(OSMArea area);

    @Update
    public abstract int update(OSMArea... area);

    @Delete
    public abstract int delete(OSMArea area);

    /* OSMBusLines */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(OSMBusLine busLine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insert(OSMBusLine... busLines);

    @Update
    public abstract int update(OSMBusLine busLine);

    @Delete
    public abstract void delete(OSMBusLine busLine);


    @Query("SELECT * FROM " + OSMBusLine.TABLE_NAME + " WHERE NOT((west > :east) OR (:west > east) OR (south > :north) OR (:south > north))")
    public abstract List<OSMBusLine> nearBusLines(double north, double south, double east, double west);

    public List<OSMBusLine> nearBusLines(Bound bound) {
        return this.nearBusLines(bound.getNorth(),bound.getSouth(),bound.getEast(),bound.getWest());
    }

    /* OSMCities */

    @Query("SELECT * FROM " + OSMCity.TABLE_NAME + " WHERE id = :id")
    public abstract OSMCity select(long id);

    @Query("SELECT * FROM " + OSMCity.TABLE_NAME + " WHERE (latitude BETWEEN :south AND :north) AND (longitude BETWEEN :west AND :east)")
    public abstract List<OSMCity> cities(double south, double west, double north, double east);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insert(OSMCity city);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] insert(OSMCity... cities);

    @Update
    public abstract int update(OSMCity city);

    @Query("DELETE FROM " + OSMCity.TABLE_NAME + " WHERE id = :id")
    public abstract int delete(long id);

}
