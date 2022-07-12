package ar.edu.unicen.isistan.asistan.storage.database.mobility.events;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class EventDao {

    // QUERIES
    @Query("SELECT COUNT(*) FROM " + Event.TABLE_NAME)
    public abstract int count();

    @Query("SELECT * FROM " + Event.TABLE_NAME + " WHERE time = :time")
    public abstract Event select(long time);

    @Query("SELECT * FROM " + Event.TABLE_NAME + " WHERE time <= :end")
    public abstract List<Event> selectUntil(long end);

    @Query("SELECT * FROM " + Event.TABLE_NAME + " ORDER BY time DESC LIMIT 1")
    public abstract Event last();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(Event event);

    @Update
    public abstract int update(Event event);

    @Query("DELETE FROM " + Event.TABLE_NAME + " WHERE time <= :until")
    public abstract int deleteUntil(long until);

}
