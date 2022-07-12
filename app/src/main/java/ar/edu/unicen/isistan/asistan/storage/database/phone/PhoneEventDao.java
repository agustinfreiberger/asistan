package ar.edu.unicen.isistan.asistan.storage.database.phone;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import android.os.AsyncTask;

import java.util.List;

@Dao
public abstract class PhoneEventDao {

    @Query("SELECT COUNT(*) FROM " + PhoneEvent.TABLE_NAME)
    public abstract int count();

    @Query("SELECT * FROM " + PhoneEvent.TABLE_NAME + " WHERE time <= :until")
    public abstract List<PhoneEvent> selectUntil(long until);

    @Query("SELECT * FROM " + PhoneEvent.TABLE_NAME + " WHERE _id = :id")
    public abstract PhoneEvent select(long id);

    @Query("SELECT * FROM " + PhoneEvent.TABLE_NAME + " WHERE type = :type ORDER BY time DESC LIMIT 1")
    public abstract PhoneEvent last(String type);

    @Insert
    public abstract long insert(PhoneEvent phone_event);

    @Insert
    public abstract long[] insert(List<PhoneEvent> phone_events);

    @Update
    public abstract int update(PhoneEvent phone_event);

    @Query("DELETE FROM " + PhoneEvent.TABLE_NAME + " WHERE time <= :until")
    public abstract int deleteUntil(long until);

    @Query("DELETE FROM " + PhoneEvent.TABLE_NAME + " WHERE _id = :id")
    public abstract int delete(long id);

    public void asyncInsert(PhoneEvent phone_event) {
        AsyncTask.execute(new PhoneEventDao.AsyncInsert(phone_event));
    }

    public void asyncInsert(List<PhoneEvent> phone_events) {
        AsyncTask.execute(new PhoneEventDao.AsyncInsertAll(phone_events));
    }

    private class AsyncInsert implements Runnable {

        private PhoneEvent phone_event;

        private AsyncInsert(PhoneEvent phone_event) {
            this.phone_event = phone_event;
        }

        @Override
        public void run() {
            PhoneEventDao.this.insert(this.phone_event);
        }
    }

    private class AsyncInsertAll implements Runnable {

        private List<PhoneEvent> phone_events;

        private AsyncInsertAll(List<PhoneEvent> phone_events) {
            this.phone_events = phone_events;
        }

        @Override
        public void run() {
            PhoneEventDao.this.insert(this.phone_events);
        }
    }
}
