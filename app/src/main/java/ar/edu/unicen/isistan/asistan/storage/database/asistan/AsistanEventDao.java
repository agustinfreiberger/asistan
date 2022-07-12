package ar.edu.unicen.isistan.asistan.storage.database.asistan;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import android.os.AsyncTask;

import java.util.List;

@Dao
public abstract class AsistanEventDao {

    @Query("SELECT * FROM " + AsistanEvent.TABLE_NAME + " WHERE time <= :until")
    public abstract List<AsistanEvent> selectUntil(long until);

    @Insert
    public abstract long insert(AsistanEvent asistanEvent);

    @Query("DELETE FROM " + AsistanEvent.TABLE_NAME + " WHERE time <= :until")
    public abstract int deleteUntil(long until);

    public void asyncInsert(AsistanEvent asistanEvent) {
        AsyncTask.execute(new AsistanEventDao.AsyncInsert(asistanEvent));
    }

    private class AsyncInsert implements Runnable {

        private AsistanEvent asistanEvent;

        private AsyncInsert(AsistanEvent asistanEvent) {
            this.asistanEvent = asistanEvent;
        }

        @Override
        public void run() {
            AsistanEventDao.this.insert(this.asistanEvent);
        }
    }

}
