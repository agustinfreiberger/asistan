package ar.edu.unicen.isistan.asistan.storage.database.wifi;

import android.os.SystemClock;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import ar.edu.unicen.isistan.asistan.utils.queues.Reusable;

@Entity(tableName = WiFiScan.TABLE_NAME)
public class WiFiScan implements Reusable<WiFiScan> {

    public static final String TABLE_NAME = "wifi_scan";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "elapsed_time")
    private long elapsedTime;
    @NotNull
    @TypeConverters(HashMapConverter.class)
    @ColumnInfo(name = "scan")
    final private HashMap<String, WiFiData> scan;

    public WiFiScan() {
        this.scan = new HashMap<>();
        this.init();
    }

    @Override
    public void init() {
        this.id = 0;
        this.time = System.currentTimeMillis();
        this.elapsedTime = SystemClock.elapsedRealtime();
        this.scan.clear();
    }

    @Override
    public void init(@Nullable WiFiScan phoneEvent) {
        if (phoneEvent != null) {
            this.id = phoneEvent.getId();
            this.time = phoneEvent.getTime();
            this.elapsedTime = phoneEvent.getElapsedTime();
            this.scan.clear();
            this.scan.putAll(phoneEvent.getScan());
        } else {
            this.init();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.time == 0;
    }

    @Override
    @NotNull
    public WiFiScan copy() {
        WiFiScan event = new WiFiScan();
        event.init(this);
        return event;
    }


    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @NotNull
    public HashMap<String, WiFiData> getScan() {
        return this.scan;
    }

    public void setScan(@Nullable HashMap<String,WiFiData> scan) {
        if (scan != null)
            this.scan.putAll(scan);
    }

    public void addWiFiData(String key, WiFiData wifiData) {
        this.scan.put(key,wifiData);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
