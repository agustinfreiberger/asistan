package ar.edu.unicen.isistan.asistan.storage.database.sensor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import android.os.AsyncTask;

import java.util.List;

@Dao
public abstract class SensorEventDao {

    @Query("SELECT COUNT(*) FROM " + PhoneSensorEvent.TABLE_NAME)
    public abstract int count();

    @Query("SELECT * FROM " + PhoneSensorEvent.TABLE_NAME)
    public abstract List<PhoneSensorEvent> all();

    @Query("SELECT * FROM " + PhoneSensorEvent.TABLE_NAME + " WHERE time >= :since AND time <= :until")
    public abstract List<PhoneSensorEvent> selectBetween(long since, long until);

    @Query("SELECT * FROM " + PhoneSensorEvent.TABLE_NAME + " WHERE _id = :id")
    public abstract PhoneSensorEvent select(long id);

    @Query("SELECT * FROM " + PhoneSensorEvent.TABLE_NAME + " WHERE sensor IS :sensor ORDER BY time DESC LIMIT 1")
    public abstract LiveData<PhoneSensorEvent> last(String sensor);

    @Insert
    public abstract long insert(PhoneSensorEvent sensorEvent);

    @Insert
    public abstract long[] insert(List<PhoneSensorEvent> sensorEvents);

    @Update
    public abstract int update(PhoneSensorEvent sensorEvent);

    @Query("DELETE FROM " + PhoneSensorEvent.TABLE_NAME + " WHERE time <= :until")
    public abstract int deleteBetween(long until);

    @Query("DELETE FROM " + PhoneSensorEvent.TABLE_NAME + " WHERE _id = :id")
    public abstract int delete(long id);

    public void asyncInsert(PhoneSensorEvent sensorEvent) {
        AsyncTask.execute(new SensorEventDao.AsyncInsert(sensorEvent));
    }

    public void asyncInsert(List<PhoneSensorEvent> sensorEvents) {
        AsyncTask.execute(new SensorEventDao.AsyncInsertAll(sensorEvents));
    }

    private class AsyncInsert implements Runnable {

        private PhoneSensorEvent sensorEvent;

        private AsyncInsert(PhoneSensorEvent sensorEvent) {
            this.sensorEvent = sensorEvent;
        }

        @Override
        public void run() {
            SensorEventDao.this.insert(this.sensorEvent);
        }
    }

    private class AsyncInsertAll implements Runnable {

        private List<PhoneSensorEvent> sensorEvents;

        private AsyncInsertAll(List<PhoneSensorEvent> sensorEvents) {
            this.sensorEvents = sensorEvents;
        }

        @Override
        public void run() {
            SensorEventDao.this.insert(this.sensorEvents);
        }
    }
}
