package ar.edu.unicen.isistan.asistan.storage.database.geolocation;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Dao
public abstract class GeoLocationDao {

    @Query("SELECT COUNT(*) FROM " + GeoLocation.TABLE_NAME)
    public abstract int count();

    @Query("SELECT * FROM " + GeoLocation.TABLE_NAME)
    public abstract List<GeoLocation> all();

    @Query("SELECT * FROM " + GeoLocation.TABLE_NAME + " WHERE time <= :until")
    public abstract List<GeoLocation> selectUntil(long until);

    @Query("SELECT * FROM " + GeoLocation.TABLE_NAME + " WHERE _id = :id")
    public abstract GeoLocation select(long id);

    @Query("SELECT * FROM " + GeoLocation.TABLE_NAME + " ORDER BY location_time DESC LIMIT 1")
    public abstract LiveData<GeoLocation> last();

    @Query("SELECT * FROM " + GeoLocation.TABLE_NAME + " WHERE trusted = 1 ORDER BY location_time DESC LIMIT 1")
    public abstract LiveData<GeoLocation> lastTrusted();

    @Query("SELECT * FROM " + GeoLocation.TABLE_NAME + " WHERE trusted = 1 ORDER BY location_time DESC LIMIT :count")
    public abstract LiveData<List<GeoLocation>> lastTrusted(int count);

    public void insert(@NotNull GeoLocation geolocation) {
        long id = this.insertGeolocation(geolocation);
        geolocation.setId(id);
    }

    @Insert
    protected abstract long insertGeolocation(GeoLocation geolocation);

    @Insert
    public abstract long[] insert(List<GeoLocation> locations);

    @Update
    public abstract int update(GeoLocation geolocation);

    @Query("DELETE FROM " + GeoLocation.TABLE_NAME + " WHERE time <= :until")
    public abstract int deleteUntil(long until);

    @Delete
    public abstract int delete(GeoLocation location);

}
