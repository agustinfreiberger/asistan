package ar.edu.unicen.isistan.asistan.storage.database.phone;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import android.os.SystemClock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import ar.edu.unicen.isistan.asistan.utils.queues.Reusable;

@Entity(tableName = PhoneEvent.TABLE_NAME)
public class PhoneEvent implements Reusable<PhoneEvent> {

    public static final String TABLE_NAME = "phone_event";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "elapsed_time")
    private long elapsedTime;
    @ColumnInfo(name = "type")
    private String type;
    @TypeConverters(HashMapConverter.class)
    @ColumnInfo(name = "extra")
    final private HashMap<String,Object> extra;

    public PhoneEvent() {
        this.extra = new HashMap<>();
        this.init();
    }

    @Ignore
    public PhoneEvent(String type) {
        this.time = System.currentTimeMillis();
        this.elapsedTime = SystemClock.elapsedRealtime();
        this.type = type;
        this.extra = new HashMap<>();
    }

    @Override
    public void init() {
        this.id = 0;
        this.time = System.currentTimeMillis();
        this.elapsedTime = SystemClock.elapsedRealtime();
        this.type = null;
        this.extra.clear();
    }

    @Override
    public void init(@Nullable PhoneEvent phoneEvent) {
        if (phoneEvent != null) {
            this.id = phoneEvent.getId();
            this.time = phoneEvent.getTime();
            this.elapsedTime = phoneEvent.getElapsedTime();
            this.type = phoneEvent.getType();
            this.extra.clear();
            this.extra.putAll(phoneEvent.getExtra());
        } else {
            this.init();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.getType() == null;
    }

    @Override
    @NotNull
    public PhoneEvent copy() {
        PhoneEvent event = new PhoneEvent();
        event.init(this);
        return event;
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

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @NotNull
    public HashMap<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(@Nullable HashMap<String,Object> extra) {
        if (extra != null)
            this.extra.putAll(extra);
    }

    public void addExtra(String key, Object extra) {
        this.extra.put(key,extra);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
