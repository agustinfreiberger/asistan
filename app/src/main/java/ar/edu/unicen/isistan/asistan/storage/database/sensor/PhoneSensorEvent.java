package ar.edu.unicen.isistan.asistan.storage.database.sensor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import android.hardware.SensorEvent;

@Entity(tableName = PhoneSensorEvent.TABLE_NAME)
public class PhoneSensorEvent {

    public static final String TABLE_NAME = "sensor_event";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;
    @ColumnInfo(name = "sensor")
    private String sensor;
    @ColumnInfo(name = "accuracy")
    private int accuracy;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "values")
    @TypeConverters(FloatArrayConverter.class)
    private float[] values;

    public PhoneSensorEvent() {
        this.sensor = null;
        this.accuracy = 0;
        this.time = 0;
        this.values = null;
    }

    public PhoneSensorEvent(SensorEvent event) {
        this.sensor = event.sensor.getName();
        this.accuracy = event.accuracy;
        this.time = event.timestamp;
        this.values = event.values;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
