package ar.edu.unicen.isistan.asistan.storage.database.activity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import android.os.AsyncTask;

import java.util.List;

@Dao
public abstract class ActivityDao {

    @Query("SELECT COUNT(*) FROM " + Activity.TABLE_NAME)
    public abstract int count();

    @Query("SELECT * FROM " + Activity.TABLE_NAME)
    public abstract List<Activity> all();

    @Query("SELECT * FROM " + Activity.TABLE_NAME + " WHERE time <= :until")
    public abstract List<Activity> selectUntil(long until);

    @Query("SELECT * FROM " + Activity.TABLE_NAME + " WHERE _id = :id")
    public abstract Activity select(long id);

    @Query("SELECT * FROM " + Activity.TABLE_NAME + " ORDER BY time DESC LIMIT 1")
    public abstract LiveData<Activity> last();

    @Query("SELECT * FROM " + Activity.TABLE_NAME + " WHERE time <= :time ORDER BY time DESC LIMIT 1")
    public abstract Activity lastBefore(long time);

    @Insert
    public abstract long insert(Activity activity);

    @Insert
    public abstract long[] insert(List<Activity> activities);

    @Update
    public abstract int update(Activity activity);

    @Query("DELETE FROM " + Activity.TABLE_NAME + " WHERE time <= :until")
    public abstract int deleteUntil(long until);

    @Delete
    public abstract int delete(Activity activity);

    public void asyncInsert(Activity activity) {
        AsyncTask.execute(new ActivityDao.AsyncInsert(activity));
    }

    public void asyncInsert(List<Activity> activities) {
        AsyncTask.execute(new ActivityDao.AsyncInsertAll(activities));
    }

    private class AsyncInsert implements Runnable {

        private Activity activity;

        private AsyncInsert(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            ActivityDao.this.insert(this.activity);
        }
    }

    private class AsyncInsertAll implements Runnable {

        private List<Activity> activities;

        private AsyncInsertAll(List<Activity> activities) {
            this.activities = activities;
        }

        @Override
        public void run() {
            ActivityDao.this.insert(this.activities);
        }
    }
}
