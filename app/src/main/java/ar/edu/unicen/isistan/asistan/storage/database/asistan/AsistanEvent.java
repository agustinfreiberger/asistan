package ar.edu.unicen.isistan.asistan.storage.database.asistan;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.os.SystemClock;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = AsistanEvent.TABLE_NAME)
public class AsistanEvent {

    public static final String TABLE_NAME = "asistan";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "elapsed_time")
    private long elapsedTime;
    @ColumnInfo(name = "component")
    private String component;
    @ColumnInfo(name = "type")
    private String type;

    public AsistanEvent() {
      this.time = System.currentTimeMillis();
      this.elapsedTime = SystemClock.elapsedRealtime();
      this.type = null;
      this.component = null;
    }

    @Ignore
    public AsistanEvent(@NotNull String component, @NotNull String type) {
      this.time = System.currentTimeMillis();
      this.elapsedTime = SystemClock.elapsedRealtime();
      this.type = type;
      this.component = component;
    }

    public long getTime() {
      return this.time;
    }

    public void setTime(long time) {
      this.time = time;
    }

    public String getType() {
      return this.type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getComponent() {
      return component;
    }

    public void setComponent(String component) {
      this.component = component;
    }

    public long getElapsedTime() {
      return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
      this.elapsedTime = elapsedTime;
    }

    public long getId() {
      return id;
    }

    public void setId(long id) {
    this.id = id;
  }

}
